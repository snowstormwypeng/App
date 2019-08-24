package enjoy.Device;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.smdt.facesdk.mipsFaceFeature;
import com.smdt.facesdk.mipsFaceInfoTrack;
import com.smdt.facesdk.mipsFaceVerifyInfo;
import com.smdt.facesdk.mipsFaceVipDB;
import com.smdt.facesdk.mipsVideoFaceTrack;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Helper.FaceCanvasView;
import Helper.MIPSCamera;
import Helper.Msgbox;
import enjoy.activitys.ActivityMain;
import enjoy.app.R;

public class FaceDevice extends ContextWrapper {
    private mipsVideoFaceTrack mfaceTrackLiveness = new mipsVideoFaceTrack();
    private String VIP_DB_PATH="";
    private String licPath= Environment.getExternalStorageDirectory().getPath() + "/mipsLic/mipsAi.lic";
    private static String[] arr_adapter_cam_res={"请接入摄像头"};

    protected int mCameraInit = 0;
    private int mcntCurFace=0;
    private long timeBak=0;
    private int framePerSec=0;
    final int VIP_FACE_CNT_MAX=20000;
    private volatile boolean mIsTracking=false; // 是否正在进行track操作
    protected MIPSCamera mMipsCameera=null;
    protected MIPSCamera mMipsCameeraIR=null;

    int PREVIEW_WIDTH=0;
    int PREVIEW_HEIGHT=0;
    private boolean killed = false;
    private Thread mTrackThread;
    private byte nv21[];
    private byte tmp[];
    private boolean isNV21ready = false;
    private byte nv21IR[];
    private byte tmpIR[];
    private boolean isNV21readyIR = false;
    private int flgFaceChange;
    private final Lock lockFaceDb = new ReentrantLock();
    private final Lock lockFaceInfo = new ReentrantLock();
    private int mtrackLivenessID=-1;
    private long timeDebug;
    private mipsFaceVipDB[] mFaceVipDBArray;
    private mipsFaceInfoTrack[] mFaceInfoDetected;
    public List<Camera.Size> mCameraSize;

    private SurfaceHolder mVidioViewHolder = null;
    private SurfaceHolder mVidioViewIrHolder = null;
    SurfaceView mSurfaceviewCamera = null;


    /**
     * 构造函数
     * @param base
     * @throws Exception
     */
    public FaceDevice(Context base) throws Exception {
        super(base);
        int ret = mfaceTrackLiveness .mipsInit(base, 1,0,licPath, 0);
        if(ret < 0)
        {
            Log.d("yunboa","mipsInit failed , ret "  + ret);
            throw new Exception("初始化失败");
        }
        //mfaceTrackLiveness.initFaceDB(context,VIP_DB_PATH+"mipsVipFaceDB",VIP_DB_PATH+"image",1);

        mfaceTrackLiveness.mipsSetSimilarityThrehold(0.8f);
        mfaceTrackLiveness.mipsSetFaceWidthThrehold(120);
        mfaceTrackLiveness.mipsEnableRefreshFaceRect();
        mfaceTrackLiveness.initFaceDB(base,VIP_DB_PATH+"mipsVipFaceDB",VIP_DB_PATH+"image",1);
        mfaceTrackLiveness.mipsEnableVipFaceVerify();
        mfaceTrackLiveness.mipsEnableFaceMoveDetect();
        mfaceTrackLiveness.mipsSetFaceMoveRitio_THRESHOLD(25);
        mfaceTrackLiveness.mipsEnableIDFeatureCropFace();
        mfaceTrackLiveness.mipsSetIDProCropFaceBorderwidth(50);

        mfaceTrackLiveness.mipsEnableFaceVerifyArea();
        mfaceTrackLiveness.mipsSetFaceVerifyArea(new Rect(400,100,880,620));
        mfaceTrackLiveness.mipsFaceVerifyAreaAutoReset();

        mfaceTrackLiveness.mipsSetFaceWidthThrehold(20);



        VIP_DB_PATH=base.getFilesDir().getAbsolutePath()+File.separator;
        mfaceTrackLiveness.initFaceDB(base,VIP_DB_PATH+"mipsVipFaceDB",VIP_DB_PATH+"image",1);

    }

    /***
     * 是否启动人脸库识别
     * @param enabled
     */
    public void EnabledVerify(boolean enabled)
    {
        if (enabled)
        {
            //通用+活体
            mfaceTrackLiveness.mipsEnableFaceVerifyArea();
            mfaceTrackLiveness.mipsSetFaceVerifyArea(new Rect(400,100,880,620));
            mfaceTrackLiveness.mipsFaceVerifyAreaAutoReset();
        }
        else
        {
            mfaceTrackLiveness.mipsDisableVipFaceVerify();
        }
    }

    /**
     * 保存当前人脸到文件
     * @param filePath 文件名
     * @return
     */
    public int saveFaceDB(String filePath)
    {
        return mfaceTrackLiveness.saveFaceDB(filePath);
    }

    /**
     * 添加一张人脸（图片）到人脸库
     * @param vipFac
     * @return
     */
    public int addPhotoToDB(mipsFaceVipDB vipFac)
    {
        return mfaceTrackLiveness.addOneFaceToDB(this,vipFac);
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
        return  mfaceTrackLiveness.addOneFaceToDB(this,feature,idxInFaceDB);
    }

    /**
     * 删除指定ID的人脸库
     * @param idxInFaceDB 人脸ID
     * @return
     */
    public int deleteOneFaceFrDB(int idxInFaceDB)
    {
        return mfaceTrackLiveness.deleteOneFaceFrDB(this,idxInFaceDB);
    }

    /**
     * 删除人脸库所有数据
     * @return
     */
    public int deleteAllFaceFrDB()
    {
        return mfaceTrackLiveness.deleteAllFaceFrDB(this);
    }

    /**
     * 校验输入 VIP照片质量，返回照片质量最佳的序号（从 0计数）
     * @param imagePath 待校验照片路径
     * @param imageCnt 照片数
     * @return
     */
    public int verifyVipImage(String[] imagePath,int imageCnt)
    {
        return mfaceTrackLiveness.verifyVipImage(imagePath,imageCnt);
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
        return mfaceTrackLiveness.verifyVipImage(sourceBitmapFace);
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
        return mfaceTrackLiveness.mipsVerifyInFaceDB(feature);
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
        return mfaceTrackLiveness.mipsVerifyInFaceDB(frameImage,frame_width,frame_height);
    }

    /**
     * 获取人脸的特征向量
     * @param bitmap
     * @return
     */
    public mipsFaceFeature mipsGetFeature(Bitmap bitmap)
    {
        return mfaceTrackLiveness.mipsGetFeature(bitmap);
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
        return mfaceTrackLiveness.mipsGetFeature(frameImage,frame_width,frame_height);
    }

    /**
     * 获取人脸库数量
     * @return
     */
    public int mipsGetDbFaceCnt()
    {
        return mfaceTrackLiveness.mipsGetDbFaceCnt();
    }

    /**
     * 设置活体检测模式；(默认：1)，即快速识别， 仅支持双目
     * @param mod  1：快速活体识别，仅支持双目 0：非快速活体识别
     */
    public void mipsSetLivenessMode(int mod)
    {
        mfaceTrackLiveness.mipsSetLivenessMode(mod);
    }

    /**
     * 获取活体检测模式
     * @return
     */
    public int mipsGetLivenessMode()
    {
        return mfaceTrackLiveness.mipsGetLivenessMode();
    }

    /**
     * 添加照片到人脸库
     * @param bmp
     * @param cardNo
     * @return
     * @throws FaceException
     */
    public int addFaceImg(Bitmap bmp,String cardNo) throws FaceException {


        mipsFaceFeature info;
        try {
            info = mipsGetFeature(bmp);
        } catch (Exception e) {
            throw new FaceException(1, "该照片没有有效的人脸特征");
        }
        int id = mipsVerifyInFaceDB(info);
        if (id == -3) {
            id = mipsGetDbFaceCnt();
            //String imgpath = Environment.getExternalStorageDirectory().getPath() + String.format("/faceVIP/imageVIP/%s.jpg", memberNo);
            mipsFaceVipDB faceVipImg = new mipsFaceVipDB(cardNo, id, bmp);
            faceVipImg.setVipID(0);
            id = addPhotoToDB(faceVipImg);
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
        if(mfaceTrackLiveness == null) {
            return -1;
        }
        mipsSetLivenessMode(1);
        EnabledVerify(true);
        return mfaceTrackLiveness.mipsGetFaceLivenessState();
    }

    /**
     * 释放资源
     */
    public void mipsUninit()
    {
        mfaceTrackLiveness.mipsUninit();
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
    private Camera.PreviewCallback mMipsCameeraCall = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (data == null) {
                    return; // 切分辨率的过程中可能这个地方的data为空
                }
                if (!mIsTracking) {
                    synchronized (nv21) {
                        try {
                            isNV21ready = true;
                            System.arraycopy(data, 0, nv21, 0, data.length);
                            isNV21ready = true;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        //Log.i(TAG, "onPreviewFrame: " + data);
                    }
                    synchronized (mTrackThread) {
                        mTrackThread.notify();
                    }
                }
                mMipsCameera.addCallbackBuffer(data); // 将此预览缓冲数据添加到相机预览缓冲数据队列里
            }
        };
    private Camera.PreviewCallback mMipsCameeraIrCall = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (data == null) {
                return; // 切分辨率的过程中可能这个地方的data为空
            }
            if(nv21IR == null) {
                nv21IR = new byte[data.length];
            }
            if(tmpIR == null) {
                tmpIR = new byte[data.length];
            }
            if (!mIsTracking) {
                synchronized (nv21IR) {
                    System.arraycopy(data, 0, nv21IR, 0, data.length);
                    isNV21readyIR = true;
                    //Log.i(TAG, "onPreviewFrame: " + data);
                }
                synchronized (mTrackThread) {
                    mTrackThread.notify();
                }
            }
            mMipsCameeraIR.addCallbackBuffer(data); // 将此预览缓冲数据添加到相机预览缓冲数据队列里
        }
    };

    private int openCamera()
    {

        int cameraCount =  Camera.getNumberOfCameras();
        Log.d("whw","输出相机的数量" +cameraCount);
        if(mCameraInit != 0)
        {
            return cameraCount;
        }

        if(mMipsCameera == null)
        {
            mMipsCameera = new MIPSCamera();
            mMipsCameera.setPreviewCallback(mMipsCameeraCall);
        }
        if(mMipsCameera != null ){
            //mMipsCameera.openCamera1(CameraInfo.CAMERA_FACING_BACK);
            mMipsCameera.openCamera1(MIPSCamera.CameraFacing);
        }



        if(mMipsCameeraIR == null && cameraCount >1 )
        {
            mMipsCameeraIR = new MIPSCamera();
            mMipsCameeraIR.setPreviewCallback(mMipsCameeraIrCall);
        }
        if(mMipsCameeraIR != null ){
            //mMipsCameeraIR.openCamera1(CameraInfo.CAMERA_FACING_FRONT);
            if(MIPSCamera.CameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mMipsCameeraIR.openCamera1(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            else
            {
                mMipsCameeraIR.openCamera1(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
        }

        return cameraCount;
    }
    private int checkCameraSize(int w, int h)
    {
        if(mCameraSize == null)
        {
            return -1;
        }
        for(int i=0; i<mCameraSize.size();i++)
        {
            if((mCameraSize.get(i).width == w) && (mCameraSize.get(i).height == h))
            {
                return i;
            }
        }
        return -1;
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(mMipsCameera != null){
                if(holder != null)
                {


                }
                else
                {
                    mMipsCameera.startPreview(null);
                }
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private int startCamera(int width, int height, SurfaceHolder holder, SurfaceHolder holderIR, int rotation)
    {
        if(mCameraInit != 0)
        {
            return 0;
        }
        if(width > 0 && height > 0)
        {
            PREVIEW_WIDTH = width;
            PREVIEW_HEIGHT = height;
        }

        mMipsCameera.initPreviewSize(PREVIEW_WIDTH,PREVIEW_HEIGHT);
        if(mCameraInit==0 ) {
            int cameraCount = Camera.getNumberOfCameras();
            if(mMipsCameera != null){
                if(holder != null)
                {
                    //mMipsCameera.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK,PREVIEW_WIDTH,PREVIEW_HEIGHT);
                    mMipsCameera.initPreviewBuffer();
                    mMipsCameera.setCameraDisplayOrientation(rotation);
                    mMipsCameera.startPreview(mVidioViewHolder);

                    if(mVidioViewHolder != null ) {
                        mVidioViewHolder.addCallback(mSurfaceCallback);
                    }
                }
                else
                {
                    mMipsCameera.initPreviewBuffer();
                    mMipsCameera.setCameraDisplayOrientation(rotation);
                    mMipsCameera.startPreview(null);

                }
            }
            if(mMipsCameeraIR != null && cameraCount > 1) {
                mMipsCameeraIR.initPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                if (holderIR != null) {
                    //mMipsCameera.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK,PREVIEW_WIDTH,PREVIEW_HEIGHT);
                    mMipsCameeraIR.initPreviewBuffer();
                    mMipsCameeraIR.setCameraDisplayOrientation(rotation);
                    mMipsCameeraIR.startPreview(mVidioViewIrHolder);
                    //openCamera(CameraFacing,mSurfaceHolderDisplay);
                } else {
                    //mMipsCameera.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK,PREVIEW_WIDTH,PREVIEW_HEIGHT);
                    mMipsCameeraIR.initPreviewBuffer();
                    mMipsCameeraIR.setCameraDisplayOrientation(rotation);
                    mMipsCameeraIR.startPreview(null);
                    //openCamera(CameraFacing,mSurfaceHolder);
                }
            }
        }


        mCameraInit =1;
        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        tmp = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        return 0;
    }

    /**
     * 停止当前的画面
     */
    public void StopView()
    {
        mMipsCameera.stopPreview();
        mMipsCameeraIR.stopPreview();
    }

    /**
     * 启动当前的画面
     */
    public void StartView()
    {
        mMipsCameera.startPreview(mVidioViewHolder);
        mMipsCameeraIR.startPreview(mVidioViewIrHolder);

        mMipsCameera.initPreviewBuffer();
        mMipsCameera.setCameraDisplayOrientation(0);

        mMipsCameeraIR.initPreviewBuffer();
        mMipsCameeraIR.setCameraDisplayOrientation(0);
    }

    public void start(SurfaceView vidioView, SurfaceView vidioViewIr, final IFaceBrushCallBack brushEvent)
    {
        if (vidioView!=null) {
            mVidioViewHolder = vidioView.getHolder();
        }
        if (vidioViewIr!=null)
        {
            mVidioViewIrHolder = vidioViewIr.getHolder();
        }
        int camcnt=openCamera();
        isNV21readyIR = true;
        startCamera(1280,720,mVidioViewHolder,mVidioViewIrHolder,0);

        mipsSetLivenessMode(1);

         mTrackThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!killed) {
                        if (( isNV21readyIR && mipsGetFaceLivenessState() > 1)||
                                (isNV21ready && mipsGetFaceLivenessState() < 2)) {
                            mIsTracking = true;
                            synchronized (nv21) {
                                System.arraycopy(nv21, 0, tmp, 0, nv21.length);
                            }

                            if(mMipsCameeraIR != null && mipsGetFaceLivenessState()>1){
                                if((nv21IR == null)||(tmpIR == null))
                                {
                                    continue;
                                }
                                synchronized (nv21IR) {
                                    System.arraycopy(nv21IR, 0, tmpIR, 0, nv21IR.length);
                                }
                                lockFaceDb.lock();
                                flgFaceChange = mfaceTrackLiveness.mipsDetectOneFrame(tmp, PREVIEW_WIDTH, PREVIEW_HEIGHT, tmpIR, PREVIEW_WIDTH, PREVIEW_HEIGHT, mtrackLivenessID);
                                lockFaceDb.unlock();
                            }else
                            {
                                lockFaceDb.lock();
                                flgFaceChange = mfaceTrackLiveness.mipsDetectOneFrame(tmp, PREVIEW_WIDTH, PREVIEW_HEIGHT, mtrackLivenessID);
                                lockFaceDb.unlock();
                            }

                            if (timeBak == 0) {
                                timeBak = System.currentTimeMillis();
                            }
                            if ((System.currentTimeMillis() - timeBak) > 1000) {
                                //Log.i(TAG, "frameRate: " + framePerSec);
                                framePerSec = 0;
                                timeBak = System.currentTimeMillis();
                            }
                            framePerSec++;

                            if (flgFaceChange == 1) {

                                mcntCurFace = mfaceTrackLiveness.mipsGetFaceCnt();
                                if (mcntCurFace==1) {
                                    //mdbFaceCnt = mfaceTrackLiveness.mipsGetDbFaceCnt();
                                    mFaceInfoDetected = mfaceTrackLiveness.mipsGetFaceInfoDetected();
                                    if (mipsGetFaceLivenessState() > 0) {
                                        mtrackLivenessID = getLivenessTrackID(mFaceInfoDetected);
                                    }
                                    if (brushEvent != null && mFaceInfoDetected[0].flgSetVIP>0
                                            && mFaceInfoDetected[0].name!=null
                                            && (!mFaceInfoDetected[0].name.equals("")) ) {
                                        FaceEntity faceEntity = new FaceEntity();
                                        String vid=String.format("%d",mFaceInfoDetected[0].flgSetVIP);
                                        faceEntity.setCardNo(mFaceInfoDetected[0].name);
                                        faceEntity.setFaceInfo(mFaceInfoDetected[0]);
                                        String path= Environment.getExternalStorageDirectory().getPath() + "/faceVIP/imageVIP" + '/' +faceEntity.getCardNo() + ".jpg";
                                        faceEntity.setImgPath(path);
                                        brushEvent.call(faceEntity);
                                    }
                                }
                                else
                                {

                                }
                                //timeDebug = System.currentTimeMillis();
                                //画脸轮廓
                            }
                            mIsTracking = false;
                            isNV21ready = false;
                            isNV21readyIR = false;
                        }
                        else {
                            synchronized (this) {
                                mTrackThread.wait(100); // 数据没有准备好就等待
                            }
                        }
                    }
                    //Log.i(TAG, "mTrackThread exit: ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //stopThread();
                }
            }
        };
        mTrackThread.start();
    }
}
