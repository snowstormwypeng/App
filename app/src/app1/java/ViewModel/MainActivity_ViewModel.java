package ViewModel;

import android.app.Activity;
import android.app.smdt.SmdtManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.smdt.facesdk.mipsFaceFeature;
import com.smdt.facesdk.mipsFaceVipDB;

import java.io.UnsupportedEncodingException;

import Enums.MsgType;
import Factory.Factory;
import Helper.Log;
import enjoy.Device.FaceDevice;
import Helper.Msgbox;
import Listener.IAsynListener;
import ViewBind.ButtonBind;
import ViewBind.EditBind;
import ViewBind.TextBind;
import enjoy.Device.FaceEntity;
import enjoy.Device.FaceException;
import enjoy.Device.IFaceBrushCallBack;
import enjoy.Device.ScanDevice;
import enjoy.Interface.IAopDemo;
import enjoy.InterfaceImpl.AopDemo;
import enjoy.activitys.ActivityMain;
import enjoy.activitys.FaceCanvasView;
import enjoy.activitys.MainActivity;
import enjoy.app.R;

/**
 * 作者：王彦鹏 on 2018-10-22 15:27
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class MainActivity_ViewModel extends Base_ViewModel {
    private ScrollView scrollView;
    private FaceDevice faceDevice;

    IAopDemo demo=Factory.GetInstance(AopDemo.class,new Object[]{this});
    public MainActivity_ViewModel(Context base,View view) {
        super(base);
        try {
            faceDevice = new FaceDevice(this);
            img= view.findViewById(R.id.FaceImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public final TextBind Text_Info=new TextBind("第一页");
    public final TextBind Text_AopInfo=new TextBind("");
    public final ButtonBind Btn_Msg=new ButtonBind("提示框");
    public final ButtonBind Btn_Aop=new ButtonBind("AopDemo");
    public final ButtonBind Btn_Clear=new ButtonBind("清除");
    public final ButtonBind Btn_FaceVerify=new ButtonBind("人脸识别");
    public final ButtonBind Btn_Scan=new ButtonBind("扫描枪");
    public final ButtonBind Btn_Gate=new ButtonBind("闸机控制");
    public final EditBind Edt_ScanValue =new EditBind("");
    public final ButtonBind Btn_DelAll=new ButtonBind("删除全部人脸库");
    public final ButtonBind Btn_AddVip=new ButtonBind("添加人脸");
    public final ButtonBind Btn_Facecount=new ButtonBind("当前人脸库数量");


    private ImageView img;

    public void Btn_Clear_OnClick(View view)
    {
        Text_AopInfo.Text.set("");
    }
    public void Btn_Aop_OnClick(View view)
    {
        if (scrollView==null) {
            scrollView = ((Activity) getBaseContext()).findViewById(R.id.scrollView);
        }
        String info="接口最终返回数据："+demo.GetData("ID","Name");
        info=Text_AopInfo.Text.get()+"\r\n"+info;
        Text_AopInfo.Text.set(info);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
    public void Btn_Msg_OnClick(View view)
    {
        Msgbox.Show(getBaseContext(),  "这是一个弹框提示。",MsgType.msg_Hint, new IAsynListener() {
                    @Override
                    public void onFinish(Object sender, Object data) {
                        Text_Info.Text.set("点击了确定");
                    }

                    @Override
                    public void onError(Object sender, Exception e) {

                    }
                }
        );
    }
    private IFaceBrushCallBack FaceEvent= new IFaceBrushCallBack(){
        @Override
        public void  faceEvent(int faceCnt)
        {

        }
        @Override
        public void call(final FaceEntity faceEntity) {
            if (faceEntity.getFaceInfo().flgLiveness==1 && faceEntity.getFaceInfo().flgSetLiveness==1 &&
                faceEntity.getFaceInfo().flgSetVIP==1)
            {
                img.setImageBitmap(faceEntity.getFaceInfo().mfaceFeature.mBitmapFace);
//                faceDevice.mipsFaceService.saveBitmapAsFile(path,
//                        String.format("%s.jpg", PublicDefine.enjoyCard.GetCardNo() + ""),
            }
//            Bitmap sourceBitmapFace = BitmapFactory.decodeFile(faceEntity.getImgPath());
//            if (sourceBitmapFace != null) {
//                img.setImageBitmap(sourceBitmapFace);
//            }

//            if (faceEntity.isVip()) {
//
//                Message m=new Message();
//                m.obj=new IAsynListener() {
//                    @Override
//                    public void onFinish(Object sender, Object data) {
//                        Msgbox.Show(getBaseContext(),String.format( "扫到会员人脸，卡号：%s",faceEntity.getCardNo()));
//                    }
//
//                    @Override
//                    public void onError(Object sender, Exception e) {
//
//                    }
//                };
//                mHandler.sendMessage(m);
//                faceDevice.StopView();
//
//                Message msg = new Message();
//                msg.obj = new IAsynListener() {
//                    @Override
//                    public void onFinish(Object sender, Object data) {
//                        faceDevice.StartView();
//                    }
//
//                    @Override
//                    public void onError(Object sender, Exception e) {
//
//                    }
//                };
//                Log.write("Face","找到会员");
//                mHandler.sendMessageDelayed(msg, 2000);
//            }
//            else
//            {
//                Log.write("Face","不是会员");
//            }
        }
    };

    public void Btn_FaceVerify_OnClick(View view)
    {
        try {

            faceDevice.EnabledVerify(true);

            FaceCanvasView faceCanvasView=(FaceCanvasView)((Activity)getBaseContext()).findViewById(R.id.canvasview_draw);
            faceDevice.start((SurfaceView)((Activity)getBaseContext()).findViewById(R.id.surfaceViewCameraIR),faceCanvasView,3,
                    FaceEvent);
        }
        catch (Exception e) {
            Msgbox.Show(this,e.getMessage());
        }
    }
    public void Btn_Scan_OnClick(View view)
    {
        ScanDevice scanDevice=new ScanDevice(this, new IAsynListener() {
            @Override
            public void onFinish(Object sender, Object data) {
                try {
                    Log.write("111",data.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Object sender, Exception e) {

            }
        });
    }
    SmdtManager smdt = SmdtManager.create(this);
    public void Btn_Gate_OnClick(View view)
    {
        smdt = SmdtManager.create(this);
        smdt.setRelayIoMode(1,1); //非自动模式，延迟参数无效
        smdt.setRelayIoValue(1); //打开继电器
        //smdt.setRelayIoValue(0); //关闭继电器

    }
    public void Btn_DelAll_OnClick(View view)
    {
        int res= faceDevice.deleteAllFaceFrDB();
        if (res==0)
        {
            Msgbox.Show(this.getBaseContext(), "删除成功");
        }
        else{
            Msgbox.Show(this.getBaseContext(), String.format("删除失败[%d]",res));
        }
    }
    private int flg=0;

    Thread AddThread=new Thread(new Runnable() {
        @Override
        public void run() {
          while (flg==0)
          {
              faceDevice.deleteAllFaceFrDB();
                Message msg=new Message();
                msg.obj=new IAsynListener() {
                    @Override
                    public void onFinish(Object sender, Object data) {
                        String path="/storage/emulated/0/app1/faceVIP/2659958626.jpg";
                        AddVipImg(path);
                    }

                    @Override
                    public void onError(Object sender, Exception e) {

                    }
                };
              mHandler.sendMessage(msg);
              try {
                  Thread.sleep(5000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              };
          }
        }
    });

    public void Btn_AddVip_OnClick(View view)
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        int requestCode=1;
        ((ActivityMain)getBaseContext()).startActivityForResult(photoPickerIntent, 1);
        String path="/storage/emulated/0/app1/faceVIP/2659958626.jpg";
        AddVipImg(path);
        //AddThread.start();
    }

    public void AddVipImg(String imgpath)
    {
        try {
            int vid=faceDevice.addFaceImg(imgpath,"2659958626",null);
            //faceDevice.mipsUninit();
            //faceDevice.Init();
            if (vid>=0) {
                Msgbox.Show(this.getBaseContext(), "添加成功");
            }
            else
            {
                Msgbox.Show(this.getBaseContext(), String.format("添加失败：[%d]",vid));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Msgbox.Show(this.getBaseContext(), e.getMessage());
        }

    }
    public void Btn_Facecount_OnClick(View view)
    {
        int c=faceDevice.mipsGetDbFaceCnt();
        Msgbox.Show(this.getBaseContext(), String.format("当前库人脸数量：[%d]",c));
    }
}
