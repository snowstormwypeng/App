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
import android.os.IBinder;
import android.util.Log;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Helper.EnjoyTools;
import Helper.MIPSCamera;
import enjoy.Service.MipsIDFaceProService;
import enjoy.activitys.FaceCanvasView;

public class FaceDevice extends ContextWrapper implements ServiceConnection {

    private String licPath= Environment.getExternalStorageDirectory().getPath() + "/mipsLic/mipsAi.lic";



    private int surface_left=0;
    private int surface_right=0;
    private int surface_top=0;
    private int surface_bottom=0;
    private int camera_w=0,camera_h=0;

    private int mcntCurFace=0;

    private FaceCanvasView mOverlayCamera=null;
    private Intent mIntent;
    private MipsIDFaceProService mipsFaceService;
    private IFaceBrushCallBack brushEvent;

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
        return mipsFaceService.mfaceTrackLiveness.addOneFaceToDB(this,vipFac);

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
        return mipsFaceService.mfaceTrackLiveness.deleteAllFaceFrDB(this);

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
        return mipsFaceService.mfaceTrackLiveness.mipsGetDbFaceCnt();
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
                FileOutputStream fs = new FileOutputStream(newPath + FileName);
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
    public int addFaceImg(String bmpFileName,String cardNo) throws FaceException {
        Bitmap bmp= BitmapFactory.decodeFile(bmpFileName);
        mipsFaceFeature info;
        try {
            info = mipsGetFeature(bmp);
        } catch (Exception e) {
            throw new FaceException(1, "该照片没有有效的人脸特征");
        }
        int id = mipsVerifyInFaceDB(info);
        if (id == -3) {
            id = mipsGetDbFaceCnt();
            String imgpath = Environment.getExternalStorageDirectory().getPath() + "/faceVIP/imageVIP/";
            copyFile(bmpFileName,imgpath,cardNo+".jpg");
            mipsFaceVipDB faceVipImg = new mipsFaceVipDB(cardNo, id, bmp);
            faceVipImg.imagePath=imgpath+cardNo+".jpg";
            faceVipImg.setVipID(1);
            id = addPhotoToDB(faceVipImg);
            //id =addOneFaceToDB(info.mfaceFeature,id);
            return id;
        }
        if (id >= 0) {
            throw new FaceException(1, "该人脸已存在");
        } else {
            return id;
        }
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
    public void start(SurfaceView vidioView, SurfaceView vidioViewIr, FaceCanvasView faceCanvas, final IFaceBrushCallBack brushEvent)
    {
        SurfaceHolder mVidioViewHolder = null;
        SurfaceHolder mVidioViewIrHolder = null;

        if (vidioView!=null) {
            mVidioViewHolder = vidioView.getHolder();
        }
        if (vidioViewIr!=null)
        {
            mVidioViewIrHolder = vidioViewIr.getHolder();
        }
        if (faceCanvas!=null)
        {
            mOverlayCamera=faceCanvas;
            mOverlayCamera.setCavasReversePortrait();
            mOverlayCamera.setOverlayRect(vidioViewIr.getLeft(),vidioViewIr.getRight(),vidioViewIr.getTop(),vidioViewIr.getBottom(),720,1280);


        }

        this.brushEvent=brushEvent;
        int res= mipsFaceService.startDetect(getBaseContext(),licPath,1280,720,
                mVidioViewHolder,mVidioViewIrHolder,
                3,getAssets(),"2");
        if (res>=0)
        {
            mOverlayCamera=faceCanvas;
            mipsFaceService.mipsSetOverlay(faceCanvas);
            setCameraState(3);
            mipsFaceService.mfaceTrackLiveness.mipsSetTrackReversePortrait();
        }
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
                    Helper.Log.write("FaceCheck", String.format("真人：%d  VIP:%d  facedbID:%d", faceInfo[0].flgLiveness,
                            faceInfo[0].flgSetVIP, faceInfo[0].FaceIdxDB));
                    mcntCurFace = mipsFaceService.mfaceTrackLiveness.mipsGetFaceCnt();
                    if (mcntCurFace == 1 && faceInfo[0].flgLiveness == 1) {
                        if (mOverlayCamera != null) {
                            mOverlayCamera.addFacesLiveness(faceInfo, FaceCanvasView.ANALYSIS_STATE);

                            mOverlayCamera.postInvalidate();
                        }

                        if (brushEvent != null) {
                            FaceEntity faceEntity = new FaceEntity();

                            if (faceInfo != null && faceInfo[0] != null && faceInfo[0].flgSetVIP > 0
                                    && faceInfo[0].name != null
                                    && (!faceInfo[0].name.equals(""))) {
                                faceEntity.setCardNo(faceInfo[0].name);
                                faceEntity.setVip(faceInfo[0].flgSetVIP > 0);
                                String path = Environment.getExternalStorageDirectory().getPath() + "/faceVIP/imageVIP" + '/' + faceEntity.getCardNo() + ".jpg";
                                faceEntity.setImgPath(path);
                            }
                            faceEntity.setFaceInfo(faceInfo[0]);

                            brushEvent.call(faceEntity);
                        }
                    } else {

                    }
                }
                catch (Exception e)
                {

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

