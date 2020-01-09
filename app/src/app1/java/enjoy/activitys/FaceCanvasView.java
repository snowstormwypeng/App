package enjoy.activitys;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.smdt.facesdk.mipsFaceInfoTrack;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import enjoy.app.BuildConfig;

public class FaceCanvasView extends ImageView {
	public static int DETECT_STATE = 0;
	public static int REGISTER_STATE = 1;
	public static int RECO_STATE = 2;
	public static int ANALYSIS_STATE = 3;
	protected int mState = DETECT_STATE;
	//private ArrayList<mipsFaceInfoTrack> mFaceList;
	private ArrayList<mipsFaceInfoTrack> mFaceLivenessList;
	private boolean mFacingFront = false;
	private int mCanvasWidth;
	private int mCanvasHeight;
	private float mXRatio;
	private float mYRatio;

	private Paint mRectPaint;
	private Paint mNamePaint;
	private Paint mRectPaint2;
	public Rect mFaceVerifyRect;
	private RectF mDrawFaceRect = new RectF();
	private RectF mFaceVerifyRectF = new RectF();

	public Rect mOverRect;
	private int mCameraWidth;
	private int mCameraHeight;
	private int flgPortrait=0;
	private Lock lockFace = new ReentrantLock();
	private Context ctx;

	FaceCanvasView(Context context) {
		super(context);
		ctx = context;
		reset();
	}

	public void setFaceVerifyRect(Rect rect)
	{
		mFaceVerifyRect = new Rect(rect);
	}

	public FaceCanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		reset();
	}

	public FaceCanvasView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ctx = context;
		reset();
	}
	public void setCavasPortrait()
	{
		flgPortrait = 1;
	}

	public void setCavasLandscape()
	{
		flgPortrait = 0;
	}

	public void setCavasReversePortrait()
	{
		flgPortrait = 3;
	}

	public void setCavasReverseLandscape()
	{
		flgPortrait = 2;
	}

	public void reset() {
		lockFace.lock();
		/*
		if (mFaceList == null) {
			mFaceList = new ArrayList<mipsFaceInfoTrack>();
		}
		mFaceList.clear();*/
		if (mFaceLivenessList == null) {
			mFaceLivenessList = new ArrayList<mipsFaceInfoTrack>();
		}
		mFaceLivenessList.clear();
		mCameraWidth = 1;
		mCameraHeight = 1;

		// 矩形框
		mRectPaint = new Paint();
		mRectPaint.setColor(Color.GREEN);
		mRectPaint.setStyle(Paint.Style.STROKE);
		mRectPaint.setStrokeWidth(2);

		mRectPaint2 = new Paint();
		mRectPaint2.setColor(Color.GREEN);
		mRectPaint2.setStyle(Paint.Style.STROKE);
		mRectPaint2.setStrokeWidth(8);

		// 识别名
		mNamePaint = new Paint();
		mNamePaint.setColor(Color.GREEN);
		mNamePaint.setTextSize(20);
		mNamePaint.setStyle(Paint.Style.FILL);
		lockFace.unlock();
	}

	public void setCameraSize(int cameraWidth, int cameraHeight) {
		mCameraWidth = cameraWidth;
		mCameraHeight = cameraHeight;
	}

	public void setOverlayRect(int left, int right, int top, int bottom,int camWidth, int camHeight) {
		mOverRect = new Rect(left, top, right, bottom);
		mCameraWidth = camWidth;
		mCameraHeight = camHeight;
		mXRatio = (float)mOverRect.width()/(float)mCameraWidth;
		mYRatio = (float)mOverRect.height()/(float)mCameraHeight;
	}
	/*
        public void addFaces(mipsFaceInfoTrack[] faceInfo, int state) {
            lockFace.lock();
            mState = state;
            mFaceList.clear();
            if (faceInfo == null) {
                return;
            }
            for(int i=0; i<mipsFaceInfoTrack.MAX_FACE_CNT_ONEfRAME;i++) {
                if (faceInfo[i] == null) {
                    continue;
                }

                mipsFaceInfoTrack face=faceInfo[i];
                mFaceList.add(face);
            }
            lockFace.unlock();
        }*/
	public void addFacesLiveness(mipsFaceInfoTrack[] faceInfo, int state) {
		lockFace.lock();
		mState = state;
		mFaceLivenessList.clear();
		if (faceInfo == null) {
			return;
		}
		for(int i = 0; i< mipsFaceInfoTrack.MAX_FACE_CNT_ONEfRAME; i++) {
			if (faceInfo[i] == null) {
				continue;
			}

			mipsFaceInfoTrack face=faceInfo[i];
			mFaceLivenessList.add(face);
		}
		lockFace.unlock();
	}
	public void setState(int state) {
		mState = state;
	}

	public void setFacingFront(boolean facingFront) {
		mFacingFront = facingFront;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//mCanvasWidth = canvas.getWidth();
		//mCanvasHeight = canvas.getHeight();
		//mXRatio = (float) mCanvasWidth / (float) mCameraWidth;
		//mYRatio = (float) mCanvasHeight / (float) mCameraHeight;
		drawFaceResult(canvas);
		//DrawFaceVerifyArea(canvas);
	}
	private void DrawFaceVerifyArea(Canvas canvas)
	{
		if(mFaceVerifyRect == null || mOverRect == null)
		{
			return;
		}
		if(flgPortrait == 0) {
			mFaceVerifyRectF.left = mOverRect.left + (float) mFaceVerifyRect.left * mXRatio;
			mFaceVerifyRectF.right = mOverRect.left + (float) mFaceVerifyRect.right * mXRatio;
			mFaceVerifyRectF.top = mOverRect.top + (float) mFaceVerifyRect.top * mYRatio;
			mFaceVerifyRectF.bottom = mOverRect.top + (float) mFaceVerifyRect.bottom * mYRatio;
		}
		else if(flgPortrait == 1)
		{
			mFaceVerifyRectF.left = mOverRect.left + (float) (mCameraWidth -mFaceVerifyRect.bottom) * mXRatio;
			mFaceVerifyRectF.right = mOverRect.left + (float) (mCameraWidth -mFaceVerifyRect.top) * mXRatio;
			mFaceVerifyRectF.top = mOverRect.top + (float) mFaceVerifyRect.left * mYRatio;
			mFaceVerifyRectF.bottom = mOverRect.top + (float) mFaceVerifyRect.right * mYRatio;
		}
		if(flgPortrait == 2) {
			mFaceVerifyRectF.left = mOverRect.left + (float) (mCameraWidth -mFaceVerifyRect.right) * mXRatio;
			mFaceVerifyRectF.right = mOverRect.left + (float) (mCameraWidth -mFaceVerifyRect.left) * mXRatio;
			mFaceVerifyRectF.top = mOverRect.top + (float) (mCameraHeight -mFaceVerifyRect.bottom) * mYRatio;
			mFaceVerifyRectF.bottom = mOverRect.top + (float) (mCameraHeight -mFaceVerifyRect.top) * mYRatio;
		}
		else if(flgPortrait == 3)
		{
			mFaceVerifyRectF.left = mOverRect.left + (float) (mFaceVerifyRect.top) * mXRatio;
			mFaceVerifyRectF.right = mOverRect.left + (float) (mFaceVerifyRect.bottom) * mXRatio;
			mFaceVerifyRectF.top = mOverRect.top + (float) (mCameraHeight - mFaceVerifyRect.right) * mYRatio;
			mFaceVerifyRectF.bottom = mOverRect.top + (float) (mCameraHeight - mFaceVerifyRect.left) * mYRatio;
		}
		canvas.drawRect(mFaceVerifyRectF, mRectPaint2);
	}

	private PointF convertPoint(PointF pointIn, int flgPortrait)
	{
		PointF pointOut=null;
		if(pointIn == null)
		{
			return null;
		}
		if(flgPortrait == 0) {
			pointOut = new PointF(mOverRect.left + pointIn.x * mXRatio,mOverRect.top + pointIn.y * mYRatio);
		}
		else if(flgPortrait == 1)
		{
			pointOut = new PointF(mOverRect.left + (mCameraWidth -pointIn.y) * mXRatio,mOverRect.top + pointIn.x * mYRatio);
		}
		if(flgPortrait == 2) {
			pointOut = new PointF(mOverRect.left + (mCameraWidth -pointIn.x) * mXRatio,mOverRect.top + (mCameraHeight -pointIn.y) * mYRatio);
		}
		else if(flgPortrait == 3)
		{
			pointOut = new PointF(mOverRect.left + pointIn.y * mXRatio,mOverRect.top + (mCameraHeight -pointIn.x) * mYRatio);
		}
		return pointOut;
	}
	private void drawtPoint(Canvas canvas, PointF pointIn, int flgPortrait, String info)
	{
		if(pointIn == null || info==null)
		{
			return ;
		}
		PointF pointOut=convertPoint(pointIn,flgPortrait);
		canvas.drawText(info, pointOut.x, pointOut.y,mNamePaint);
	}
	/**
	 * 画人脸框：与人脸检测、注册、识别相关
	 * */
	private void drawFaceResult(Canvas canvas) {
		try {

			// 清空画布
			canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			//lockFace.lock();
			if (mFaceLivenessList==null)
			{
				return;
			}
			for (mipsFaceInfoTrack faceinfo : mFaceLivenessList) {

				if (faceinfo == null) {
					continue;
				}

				if (flgPortrait == 0) {
					mDrawFaceRect.left = mOverRect.left + (float) faceinfo.faceRect.left * mXRatio;
					mDrawFaceRect.right = mOverRect.left + (float) faceinfo.faceRect.right * mXRatio;
					mDrawFaceRect.top = mOverRect.top + (float) faceinfo.faceRect.top * mYRatio;
					mDrawFaceRect.bottom = mOverRect.top + (float) faceinfo.faceRect.bottom * mYRatio;
				} else if (flgPortrait == 1) {
					mDrawFaceRect.left = mOverRect.left + (float) (mCameraWidth - faceinfo.faceRect.bottom) * mXRatio;
					mDrawFaceRect.right = mOverRect.left + (float) (mCameraWidth - faceinfo.faceRect.top) * mXRatio;
					mDrawFaceRect.top = mOverRect.top + (float) faceinfo.faceRect.left * mYRatio;
					mDrawFaceRect.bottom = mOverRect.top + (float) faceinfo.faceRect.right * mYRatio;
				}
				if (flgPortrait == 2) {
					mDrawFaceRect.left = mOverRect.left + (float) (mCameraWidth - faceinfo.faceRect.right) * mXRatio;
					mDrawFaceRect.right = mOverRect.left + (float) (mCameraWidth - faceinfo.faceRect.left) * mXRatio;
					mDrawFaceRect.top = mOverRect.top + (float) (mCameraHeight - faceinfo.faceRect.bottom) * mYRatio;
					mDrawFaceRect.bottom = mOverRect.top + (float) (mCameraHeight - faceinfo.faceRect.top) * mYRatio;
				} else if (flgPortrait == 3) {
					if (BuildConfig.FLAVOR.equals("tv800")){
						mDrawFaceRect.left = mOverRect.left + (float) (faceinfo.faceRect.top) * mXRatio;
						mDrawFaceRect.right = mOverRect.left + (float) (faceinfo.faceRect.bottom) * mXRatio;
						mDrawFaceRect.top = mOverRect.top + (float) (mCameraHeight - faceinfo.faceRect.right) * mYRatio;
						mDrawFaceRect.bottom = mOverRect.top + (float) (mCameraHeight - faceinfo.faceRect.left) * mYRatio;
					} else {
						mDrawFaceRect.left = mOverRect.left + (float) (faceinfo.faceRect.top) * mXRatio + 50*mYRatio;
						mDrawFaceRect.right = mOverRect.left + (float) (faceinfo.faceRect.bottom) * mXRatio + 80*mYRatio;
						mDrawFaceRect.top = mOverRect.top + (float) faceinfo.faceRect.right * mYRatio + 50*mYRatio;
						mDrawFaceRect.bottom = mOverRect.top + (float) faceinfo.faceRect.left * mYRatio - 100*mYRatio;
					}
				}
				mDrawFaceRect.top = mDrawFaceRect.top - 20 * mXRatio;
				mDrawFaceRect.left = mDrawFaceRect.left - 10 * mXRatio;
				mDrawFaceRect.right = mDrawFaceRect.right - 20 * mXRatio;
				mDrawFaceRect.bottom = mDrawFaceRect.bottom + 20 * mXRatio;

				canvas.drawRect(mDrawFaceRect, mRectPaint);
				// 画识别名
				String name = "会员：识别中...";

				if (mState == ANALYSIS_STATE) {
					float dy = mDrawFaceRect.top - 30;
					//name += "检测评分:" + faceinfo.mfaceScore;
					//name += ",";

//				if (faceinfo.livenessDetectedCnt != 0) {
//					name += faceinfo.livenessDetectedCnt;
//					name += ",";
//				}
//
					if (faceinfo.flgFaceMotionless > 0) {
						name = "会员：识别中...";
					} else {
						name = "请不要移动";
						canvas.drawText(name, mDrawFaceRect.left, mDrawFaceRect.top - 30,
								mNamePaint);
					}


					if (faceinfo.FaceIdxDB >= 0) {
//						FaceCardDataAccess faceCardDataAccess = new FaceCardDataAccess(ctx);
//						FaceCard faceCard = faceCardDataAccess.queryById(Integer.valueOf(faceinfo.name));
//						name = "会员:" + faceCard.getCardNo();
						name = "会员:" + faceinfo.name;
//					name += faceinfo.name;
//					name += ",";
//					//name += "相似度:"+faceinfo.mfaceSimilarity;
//					//name += "相似度:"+String.format("%.2f",faceinfo.mfaceSimilarity);
//					name += String.format("%.2f",faceinfo.mfaceSimilarity);
//					name += ",";
//					//name += "集合:"+faceinfo.similaritySet;
//					name += faceinfo.similaritySet;
//					name += ",";
//					//name += "搜索人数:"+faceinfo.serchFaceCount;
//					name += faceinfo.serchFaceCount;
//					name += ",";
//
//					dy = mDrawFaceRect.top - 100;
					} else if (faceinfo.flgSetVIP == 0) {
						name = "会员：识别中...";
						//name += ",";
						dy = mDrawFaceRect.top - 30;
					} else {
						name = "会员：识别中...";
						dy = mDrawFaceRect.top - 30;
					}
//				if(faceinfo.flgSetLiveness == 1) {
//					if(faceinfo.flgLiveness == 1)
//					{
//						name += "真人";
//					}
//					else
//					{
//						name += "图片";
//					}
//
//					if(faceinfo.mVipSearchTimeDebug > 0)
//					{
//						name += "S:";
//						name += faceinfo.mVipSearchTimeDebug;
//						name += "ms";
//					}
//					if(faceinfo.LivenessTimeDebugDetect > 0)
//					{
//						name += ",D:";
//						name += faceinfo.LivenessTimeDebugDetect;
//					}
//					if(faceinfo.LivenessTimeDebugColor > 0)
//					{
//						name += "ms,color:";
//						name += faceinfo.LivenessTimeDebugColor;
//					}
//					if(faceinfo.LivenessTimeDebugGrey > 0)
//					{
//						name += "ms,grey:";
//						name += faceinfo.LivenessTimeDebugGrey;
//						name += "ms";
//					}
//				}


					if (faceinfo.flgSetAttr == 1) {
						String analysisInfo = getGenderAgeInfo(faceinfo);
						//Log.e("yunboa","flgRefreshFaceAttr ,analysisInfo:" + analysisInfo);
						name += analysisInfo;
					}
					canvas.drawText(name, mDrawFaceRect.left, mDrawFaceRect.top - 10,
							mNamePaint);

//				if(true)
//				{
//					String name1 = "";
//					if(faceinfo.LivenessTimeDebug > 0) {
//						name1 += "活体:"+faceinfo.LivenessTimeDebug;
//						name1 += "ms,";
//					}
//					if(faceinfo.vipTimeDebug > 0) {
//						name1 += "vip:"+faceinfo.vipTimeDebug;
//						name1 += "ms,";
//					}
//					if(faceinfo.faceattrTimeDebug > 0) {
//						name1 += "属性:"+faceinfo.faceattrTimeDebug;
//						name1 += "ms,";
//					}
//					if(faceinfo.allTimeDebug > 0) {
//						name1 += "总共:"+faceinfo.allTimeDebug;
//						name1 += "ms,";
//					}
//					canvas.drawText(name1, mDrawFaceRect.left, mDrawFaceRect.top -30- 10,
//							mNamePaint);
//				}
				}
			}
			//lockFace.unlock();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getGenderAgeInfo(mipsFaceInfoTrack faceinfo) {
		StringBuilder builder = new StringBuilder();
		//builder.append("性别：");
		if(faceinfo.isMale == 1) {
			builder.append("男");
		}else if(faceinfo.isMale > 50) {
			builder.append("男");
		}
		else
		{
			builder.append("女");
		}

		builder.append(",");
		builder.append(faceinfo.age);
		builder.append(",");
		builder.append("颜值:"+faceinfo.attrActive);
		if(faceinfo.isEyeGlass > 50) {
			builder.append(",");
			builder.append("戴眼镜");
		}
		if(faceinfo.isSunGlass > 50) {
			builder.append(",");
			builder.append("太阳镜");
		}
		if(faceinfo.isSmile > 50) {
			builder.append(",");
			builder.append("微笑");
		}
		builder.append(", (");
		builder.append(faceinfo.faceRect.width());
		builder.append(",");
		builder.append(faceinfo.faceRect.height());
		builder.append(")");



		if(faceinfo.mfaceFeature !=null) {

			builder.append(", (");
			builder.append((int) (faceinfo.mfaceFeature.mYaw));
			builder.append(",");
			builder.append((int) (faceinfo.mfaceFeature.mPitch));
			builder.append(",");
			builder.append((int) (faceinfo.mfaceFeature.mRoll));
			builder.append(")");
		}

		//builder.append(",");
		//builder.append(faceinfo.mYaw);
		//builder.append(",");
		//builder.append(faceinfo.mRoll);
		//builder.append(",");
		//builder.append(faceinfo.mPitch);
		//builder.append(",");
		//builder.append(":");
		//builder.append(faceinfo.attrActive);
		return builder.toString();
	}
}
