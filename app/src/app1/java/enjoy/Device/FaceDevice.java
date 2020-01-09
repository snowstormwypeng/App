package enjoy.Device;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.smdt.facesdk.mipsFaceFeature;
import com.smdt.facesdk.mipsFaceInfoTrack;
import com.smdt.facesdk.mipsFaceVerifyInfo;
import com.smdt.facesdk.mipsFaceVipDB;
import com.smdt.facesdk.mipsVideoFaceTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Exceptions.EException;
import Helper.EnjoyTools;
import Helper.Log;

import Listener.IAsynListener;
import Service.MipsIDFaceProService;
//import enjoy.Service.MipsIDFaceProService;
import enjoy.activitys.FaceCanvasView;
import enjoy.app.BuildConfig;

public class FaceDevice extends ContextWrapper implements ServiceConnection {

    private String licPath= Environment.getExternalStorageDirectory().getPath() + "/mipsLic/mipsAi.lic";

    private int mcntCurFace=0;

    private FaceCanvasView mOverlayCamera=null;
    private Intent mIntent;
    public MipsIDFaceProService mipsFaceService;
    private IFaceBrushCallBack brushEvent;
    public String VipImagePath= Environment.getExternalStorageDirectory().getPath() + "/" + BuildConfig.FLAVOR + "/faceVIP";
    private String LastCardNo="";
    private long LastSendTime=0;
    private Context ctx;
    //FaceCardDataAccess faceCardDataAccess;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj instanceof IAsynListener) {
                ((IAsynListener) msg.obj).onFinish(this, msg.arg1);
            }
        }
    };

    public void Init()throws Exception
    {

    }
    /**
     * 构造函数
     * @param base
     * @throws Exception
     */
    public FaceDevice(Context base) throws Exception {
        super(base);
        //Init();
        ctx = base;
        //faceCardDataAccess = new FaceCardDataAccess(getBaseContext());
        mIntent = new Intent(this, MipsIDFaceProService.class);
        startService(mIntent);
        bindService(mIntent,this, BIND_AUTO_CREATE);
    }

    /***
     * 是否启动人脸库识别
     * @param enabled
     */
    public void EnabledVerify(boolean enabled)
    {

    }
    /**
     * 验证人脸特征是否匹配
     * @param feature1
     * @param feature2
     * @return
     */
    public boolean mipsVerifyID(mipsFaceFeature feature1, mipsFaceFeature feature2) {
        if (mipsFaceService.mfaceTrackLiveness.mipsVerifyID(feature1,feature2) > 0.8){
            return true;
        } else {
            return false;
        }
    }

    //根据卡号删除人脸特征
    public int deleteFaceByCardNo(String cardNo)throws EException {

        //获取库中卡号对应的人脸照片
        String imgpath =VipImagePath + "/"+cardNo+".jpg";
        Bitmap bmp = BitmapFactory.decodeFile(imgpath);

        //获取旧照片人脸特征
        mipsFaceFeature info;
        try {
            info = mipsGetFeature(bmp);
        } catch (Exception e) {
            throw new EException(1,"删除照片时旧人脸库照片未识别到有效人脸特征");
        }
        if (info == null) {
            throw new EException(1,"删除照片时旧人脸库照片未识别到有效人脸特征");
        }

        //校验人脸库,获取id
        int id = mipsVerifyInFaceDB(info);
        if (id < 0){
            throw new EException(2,"删除照片时校验旧照片校验是否在人脸库中失败["+id+"]");
        }

        //删除人脸库
        int res = deleteOneFaceFrDB(id);
        if (res != 0){
            throw new EException(2,"删除旧人脸库照片失败["+res+"]");
        }
        Helper.Log.write("FaceDevice","删除人脸库照片成功["+cardNo+"]");
        //EnjoyTools.DeleteFile(imgpath);
        return 0;
    }


    //删除人脸特征
    public int deleteFaceByFeature(String cardNo,mipsFaceFeature info)throws EException {

        //获取库中卡号对应的人脸照片
        String imgpath =VipImagePath + "/"+cardNo+".jpg";

        //校验人脸库,获取id
        int id = mipsVerifyInFaceDB(info);
        if (id < 0){
            throw new EException(2,"删除照片时校验旧照片校验是否在人脸库中失败["+id+"]");
        }

        //删除人脸库
        int res = deleteOneFaceFrDB(id);
        if (res != 0){
            throw new EException(2,"删除旧人脸库照片失败["+res+"]");
        }
        Helper.Log.write("FaceDevice","删除人脸库照片成功["+cardNo+"]");
        //EnjoyTools.DeleteFile(imgpath);
        return 0;
    }


    /**
     * 保存当前人脸到文件
     * @param filePath 文件名
     * @return
     */
    public int saveFaceDB(String filePath)
    {
        return mipsFaceService.mipsAddVipFace(getBaseContext(),filePath);

    }

    /**
     * 添加一张人脸（图片）到人脸库
     * @param vipFac
     * @return
     */
    public int addPhotoToDB(mipsFaceVipDB vipFac)
    {
        return 0;
        //return mipsFaceService.addPhotoToDB(this,vipFac);//.mfaceTrackLiveness.addOneFaceToDB(this,vipFac);

    }


    /**
     * 描述：添加一张人脸（特征向量）到人脸库
     * @param feature 待添加的人脸特征向量，添加成功，本函 数会设置 idxInFaceDB
     * @param idxInFaceDB VIP人脸库中的 ID（需用户设置）
     * @return 0: 添加成功；
     * <0：添加失败：
     * -1：feature为空；
     * -2：SDK特征提取器异常；
     * -3：参数错误（idxInFaceDB<0或 id重 复）；
     * -9：添加人脸操作失败；
     * -10：超过最大人脸数人；
     */
    public int addOneFaceToDB(byte[] feature,int idxInFaceDB)
    {
        return mipsFaceService.mfaceTrackLiveness.addOneFaceToDB(this,feature,idxInFaceDB);

    }

    /**
     * 删除指定ID的人脸库
     * @param idxInFaceDB 人脸ID
     * @return
     */
    public int deleteOneFaceFrDB(int idxInFaceDB)
    {
        return mipsFaceService.mfaceTrackLiveness.deleteOneFaceFrDB(this,idxInFaceDB);

    }

    /**
     * 删除人脸库所有数据
     * @return
     */
    public int deleteAllFaceFrDB()
    {
        if (mipsFaceService!=null && mipsFaceService.mfaceTrackLiveness!=null) {
            //EnjoyTools.DeleteDir(VipImagePath);
            Helper.Log.write("FaceDevice","删除全部人脸库成功");
            return mipsFaceService.mfaceTrackLiveness.deleteAllFaceFrDB(this);
        }
        else
        {
            return -1;
        }
    }

    /**
     * 校验输入 VIP照片质量，返回照片质量最佳的序号（从 0计数）
     * @param imagePath 待校验照片路径
     * @param imageCnt 照片数
     * @return
     */
    public int verifyVipImage(String[] imagePath,int imageCnt)
    {
        return mipsFaceService.mfaceTrackLiveness.verifyVipImage(imagePath,imageCnt);

    }

    /**
     * 校验输入 VIP照片质量，返回照片校验详细信息（mipsFaceVerifyInfo.infoList），详细信息包括：照片分辨率、照片
     * 所有人脸人脸宽（像素点）、俯仰角、倾斜角、水平转角、人脸分数值（低于设置的阈值就不认为是人脸）以及这些详细信
     * 息的阈值。
     * @param sourceBitmapFace 待校验照片 Bitmap
     * @return 照片信息
     */
    public mipsFaceVerifyInfo verifyVipImage(Bitmap sourceBitmapFace)
    {
        return mipsFaceService.mfaceTrackLiveness.verifyVipImage(sourceBitmapFace);

    }

    /**
     * 校验摄像头的画面是否在人脸库中
     * @param feature 已提取的人脸特征
     * @return >=0：在人脸库中的 ID；
     * -3：校验完成，不在人脸库
     * -1：frameImage为空
     * -2：摄像头画面人脸检测失败
     */
    public int mipsVerifyInFaceDB(mipsFaceFeature feature)
    {
        return mipsFaceService.mfaceTrackLiveness.mipsVerifyInFaceDB(feature);
    }

    /**
     * ：校验摄像头的画面是否在人脸库中；
     * @param frameImage 从 RGB摄像头获取的一张图像的 MJPEG数据
     * @param frame_width  RGB图像的宽（像素）
     * @param frame_height  RGB图像的高（像素）
     * @return >=0：在人脸库中的 ID；
     * -3：校验完成，不在人脸库
     * -1：frameImage为空
     * -2：摄像头画面人脸检测失败
     */
    public int mipsVerifyInFaceDB(byte[] frameImage,int frame_width,int frame_height)
    {
        return mipsFaceService.mfaceTrackLiveness.mipsVerifyInFaceDB(frameImage,frame_width,frame_height);
    }

    /**
     * 获取人脸的特征向量
     * @param bitmap
     * @return
     */
    public mipsFaceFeature mipsGetFeature(Bitmap bitmap)
    {
        return  mipsFaceService.mfaceTrackLiveness.mipsGetFeature(bitmap);
    }

    /**
     * 获取人脸的特征向量
     * @param frameImage
     * @param frame_width
     * @param frame_height
     * @return
     */
    public mipsFaceFeature mipsGetFeature(byte[] frameImage,int frame_width,int frame_height)
    {
        return mipsFaceService.mfaceTrackLiveness.mipsGetFeature(frameImage,frame_width,frame_height);
    }

    /**
     * 获取人脸库数量
     * @return
     */
    public int mipsGetDbFaceCnt()
    {
        if (mipsFaceService!=null && mipsFaceService.mfaceTrackLiveness!=null) {
            return mipsFaceService.mfaceTrackLiveness.mipsGetDbFaceCnt();
        }
        else
        {
            return -1;
        }
    }

    /**
     * 获取活体检测模式
     * @return
     */
    public int mipsGetLivenessMode()
    {
        return mipsFaceService.mfaceTrackLiveness.mipsGetLivenessMode();
    }

    private void copyFile(String oldPath, String newPath,String FileName) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件不存在时
                String name = oldfile.getName();
                File targetPath = new File(newPath);
                if(!targetPath.exists()){
                    targetPath.mkdirs();
                }

                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath + "/" + FileName);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);

                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }
    /**
     * 添加照片到人脸库
     * @param bmpFileName
     * @param cardNo
     * @return
     * @throws FaceException
     */
    public int addFaceImg(String bmpFileName,String cardNo,byte[] faceFeature) throws EException {
        //删除重复卡号
//        FaceCard faceCard = faceCardDataAccess.queryByCardNo(cardNo);
//        if (faceCard != null){
//            //删除人脸库
//            deleteOneFaceFrDB(faceCard.getId());
//
//            //删除数据库
//            try {
//                faceCardDataAccess.deleteFaceCard(faceCard);
//            } catch (Exception e){
//                e.printStackTrace();
//                throw new EException("删除数据库失败");
//            }
//
//            //删除照片
//            EnjoyTools.DeleteFile(VipImagePath + "/"+cardNo+".jpg");
//        }

        if (faceFeature == null) {
            Bitmap bmp= BitmapFactory.decodeFile(bmpFileName);
            mipsFaceFeature info;
            try {
                info = mipsGetFeature(bmp);
            } catch (Exception e) {
                Helper.Log.write("addFaceImg", String.format("该照片没有有效的人脸特征:%s",cardNo));
                return 0;
            }
            if (info == null) {
                Helper.Log.write("addFaceImg", String.format("该照片没有有效的人脸特征:%s",cardNo));
                return 0;
            }
            faceFeature = info.mfaceFeature;
        }


        //若人脸库存在此特征则删除人脸库人脸
        mipsFaceFeature feature = new mipsFaceFeature(faceFeature,null);
        int faceId = mipsVerifyInFaceDB(feature);
        if (faceId >= 0){
            //删除人脸库
            deleteOneFaceFrDB(faceId);
        }


        //添加人脸库
        int id=Integer.valueOf(String.format("%s%d",String.valueOf(EnjoyTools.GetTimestamp()).substring(8), new Random().nextInt(999)));
        int res = addOneFaceToDB(faceFeature,id);
        if (res>=0) {
            Helper.Log.write("addFaceImg", "该照片添加成功：" + cardNo);
        } else {
            Helper.Log.write("addFaceImg", "该照片添加失败：" + cardNo);
        }

        //添加FaceCard库
//        faceCard = new FaceCard();
//        faceCard.setId(id);
//        faceCard.setCardNo(cardNo);
//        try{
//            faceCardDataAccess.Insert(faceCard);
//        } catch (Exception e){
//            e.printStackTrace();
//            throw new EException("删除数据库失败");
//        }

        //添加照片
        copyFile(bmpFileName,VipImagePath,cardNo+".jpg");


        return 0;
    }
    //获取VIP人脸校验状态
    public int mipsGetFaceLivenessState()
    {

        EnabledVerify(true);
        return mipsFaceService.mfaceTrackLiveness.mipsGetFaceLivenessState();
    }

    /**
     * 释放资源
     */
    public void mipsUninit()
    {
        mipsFaceService.mfaceTrackLiveness.mipsUninit();
    }


    private int getLivenessTrackID(mipsFaceInfoTrack[] info)
    {
        int faceWidthMax=0;
        int idx=-1;
        if(info == null)
        {
            return -1;
        }
        for(int i = 0; i< mipsFaceInfoTrack.MAX_FACE_CNT_ONEfRAME; i++) {
            if (info[i] == null) {
                continue;
            }
            if(info[i].livenessDetecting == 1)
            {
                return info[i].FaceTRrackID;
            }
            if(info[i].faceRect.width() > faceWidthMax)
            {
                faceWidthMax = info[i].faceRect.width();
                idx = i;
            }
        }
        if(idx >= 0)
        {
            return info[idx].FaceTRrackID;
        }

        return -1;
    }


    /**
     * 停止当前的画面
     */
    public void StopView()
    {

    }

    /**
     * 启动当前的画面
     */
    public void StartView()
    {
       // mipsFaceService.mMipsCameera.startPreview(mipsFaceService.mSurfaceHolder);
        //mipsFaceService.mMipsCameera.initPreviewBuffer();
    }

    public void setCavasOverlay(SurfaceView vidioView){
        if (mOverlayCamera != null) {
            mOverlayCamera.reset();
            mOverlayCamera.setCavasReversePortrait();
            mOverlayCamera.setOverlayRect(vidioView.getLeft(), vidioView.getRight(), vidioView.getTop(), vidioView.getBottom(), 720, 1280);
        }
    }

    private void setCameraState(int state)
    {
        if(state == 0)
        {
            if(mipsFaceService != null)
            {
                mipsFaceService.mipsSetTrackLandscape();
            }
            //mOverlayCamera.setOverlayRect(surface_left, surface_right, surface_top, surface_bottom, camera_w,camera_h);
            //mOverlayCamera.setCavasLandscape();
        }
        else if(state == 1)
        {
            if(mipsFaceService != null)
            {
                mipsFaceService.mipsSetTrackPortrait();
            }

            //mOverlayCamera.setOverlayRect(surface_left, surface_right, surface_top, surface_bottom,camera_h, camera_w);
            //mOverlayCamera.setCavasPortrait();
        }
        else if(state == 2)
        {
            if(mipsFaceService != null)
            {
                mipsFaceService.mipsSetTrackReverseLandscape();
            }

            //mOverlayCamera.setOverlayRect(surface_left, surface_right, surface_top, surface_bottom, camera_w,camera_h);
            //mOverlayCamera.setCavasReverseLandscape();
        }
        else if(state == 3)
        {
            if(mipsFaceService != null)
            {
                mipsFaceService.mipsSetTrackReversePortrait();
            }

            //mOverlayCamera.setOverlayRect(surface_left, surface_right, surface_top, surface_bottom,camera_h, camera_w);
            //mOverlayCamera.setCavasReversePortrait();
        }
    }
    public void start(SurfaceView vidioView,  FaceCanvasView faceCanvas,int rotation, final IFaceBrushCallBack brushEvent)
    {
        SurfaceHolder mVidioViewHolder = null;

        if (vidioView!=null) {
            mVidioViewHolder = vidioView.getHolder();
        }
        if (faceCanvas!=null)
        {
            faceCanvas.reset();
            mOverlayCamera=faceCanvas;
            mOverlayCamera.setCavasReversePortrait();
            mOverlayCamera.setOverlayRect(vidioView.getLeft(),vidioView.getRight(),vidioView.getTop(),vidioView.getBottom(),720,1280);
        }

        this.brushEvent=brushEvent;
        int res;

        res= mipsFaceService.startDetect(getBaseContext(),licPath,1280,720,
                    mVidioViewHolder,null,
                    rotation,getAssets(),"2",VipImagePath);
//        res= mipsFaceService.startDetect(getApplicationContext(), licPath, 1280, 720,
//                mVidioViewHolder,null, 3,getAssets(),"2");
        if (res>=0)
        {
            mOverlayCamera=faceCanvas;
            mipsFaceService.mipsSetOverlay(faceCanvas);
            setCameraState(rotation);
            if (BuildConfig.FLAVOR.equals("tv1080")) {
                mipsFaceService.mfaceTrackLiveness.mipsSetTrackPortrait();
            }
            //mipsFaceService.mfaceTrackLiveness.mipsSetTrackReversePortrait();
            mipsFaceService.mfaceTrackLiveness.mipsSetPitchAngle(15f);
            mipsFaceService.mfaceTrackLiveness.mipsDisableFaceVerifyArea();
            mipsFaceService.mfaceTrackLiveness.mipsSetMaxVipVerifyCntThrehold(3);

            //设置/获取照片人脸置信度阈值 （可设置范围 0.1~1.0，推荐 0.9~0.99）最大 0.8
            mipsFaceService.mfaceTrackLiveness.mipsSetVerifyScoreThrehold((float)0.6);

            //设置/获取人脸跟踪人脸置信度 阈值（可设置范围 0.1~1.0，推 荐 0.9~0.99）
            mipsFaceService.mfaceTrackLiveness.mipsSetFaceScoreThrehold((float)0.6);

            //设置/获取人脸对比相似度阈值 （（可设置范围 0.1~1.0，推荐 0.5~0.8））
            mipsFaceService.mfaceTrackLiveness.mipsSetSimilarityThrehold((float)0.5);

            mipsFaceService.mipsEnableRefreshFaceVIP();
        }
    }

    public void setFaceEvent(IFaceBrushCallBack brushEvent) {
        this.brushEvent=brushEvent;
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MipsIDFaceProService.Binder binder = (MipsIDFaceProService.Binder) service;
        mipsFaceService = binder.getService();
        mipsFaceService.registPoseCallback(new MipsIDFaceProService.PoseCallBack() {
            @Override
            public void onPosedetected(final String flag, final int curFaceCnt, final int cntFaceDB, final mipsFaceInfoTrack[] faceInfo)
            {
                try {
                    if (mcntCurFace!=curFaceCnt)
                    {
                        if (brushEvent != null) {
                            mcntCurFace=curFaceCnt;
                            brushEvent.faceEvent(curFaceCnt);
                        }
                    }
                    if (curFaceCnt==0 || faceInfo.length <= 0 || faceInfo[0] == null ) {
                        return;
                    }
                    Log.d("brushFace", String.format("flgSetVIP：%d   FaceIdxDB：%d  flgLiveness:%d   flgSetLiveness:%d",
                            faceInfo[0].flgSetVIP, faceInfo[0].FaceIdxDB,faceInfo[0].flgLiveness,faceInfo[0].flgSetLiveness));
                    mcntCurFace = curFaceCnt;
                    if (mcntCurFace == 1 && faceInfo[0].flgLiveness == 1) {
                        if (mOverlayCamera != null) {
                            mOverlayCamera.addFacesLiveness(faceInfo, FaceCanvasView.ANALYSIS_STATE);

                            mOverlayCamera.postInvalidate();
                        }

                        if (brushEvent != null) {
                            final FaceEntity faceEntity = new FaceEntity();
                            if (faceInfo[0].flgSetLiveness == 1) {
                                switch (faceInfo[0].FaceIdxDB) {
                                    case -1: {
                                        if ((System.currentTimeMillis() - LastSendTime) / 1000 >= 10) {
                                            LastSendTime = System.currentTimeMillis();

                                            faceEntity.setVip(false);
                                            faceEntity.setCardNo("0");
                                            faceEntity.setImgPath("");
                                            brushEvent.call(faceEntity);
                                        }
                                        break;
                                    }
                                    default: {
                                        break;
                                    }
                                }
                            }

                            if (faceInfo != null && faceInfo[0] != null && faceInfo[0].flgSetVIP > 0
                                    && faceInfo[0].name != null
                                    && (!faceInfo[0].name.equals(""))) {
//                                FaceCard faceCard = faceCardDataAccess.queryById(Integer.valueOf(faceInfo[0].name));
//                                if (faceCard == null) {
//                                    deleteOneFaceFrDB(Integer.valueOf(faceInfo[0].name));
//                                }
//                                faceEntity.setCardNo(faceCard.getCardNo());
//                                faceEntity.setVip(faceInfo[0].flgSetVIP > 0);
//                                String path = VipImagePath + "/" + faceCard.getCardNo() + ".jpg";
//                                faceEntity.setImgPath(path);
                            }
                            faceEntity.setFaceInfo(faceInfo[0]);
                            Message msg = new Message();
                            msg.arg1 = 1;
                            msg.obj = new IAsynListener() {
                                @Override
                                public void onFinish(Object sender, Object data) {
                                    brushEvent.call(faceEntity);
                                }

                                @Override
                                public void onError(Object sender, Exception e) {

                                }
                            };
                            mHandler.sendMessage(msg);
                        }
                    }
                    else {

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        refreshCamera();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
    public int refreshCamera()
    {
        int camcnt=mipsFaceService.openCamera();

        return camcnt;
    }
}
