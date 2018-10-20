package BaseActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.ActivityResult;
import Entity.RequestPermissionsResult;
import Listener.IAsynListener;
import enjoy.app.BuildConfig;
import enjoy.app.R;


/**
 * Created by 王彦鹏 on 2017-12-11.
 */

public class BaseActivity extends AppCompatActivity {

    private IAsynListener callBackListener;
    protected int TimeOutClose = 30;//自动关闭
    private class OnLongClickListenerImpl implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View view) {
            try{
                if (BuildConfig.DEBUG) {
                    //SqlliteManage sqlliteManage = new SqlliteManage(view.getContext());
                    //sqlliteManage.Show();
                }
            }catch(Exception e){
                System.out.println(e);

            }
            return true;
        }


    }

    protected   void hideBottomUIMenu(final Window window) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = window.getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener()
                    {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            hideBottomUIMenu(window);
                            //Toast.makeText(MainActivity.this,"隐藏虚拟按钮栏", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
   
    protected void StartAutoClose(int times, TextView btn)
    {
        TimeOutClose=times;
        Message msg=new Message();
        msg.what=1;
        msg.obj=btn;
        //MsgHandle.sendMessage(msg);
    }
    protected void StartAutoClose(int times, TextView btn, IAsynListener callBackListener)
    {
        TimeOutClose=times;
        this.callBackListener=callBackListener;
        Message msg=new Message();
        msg.what=1;
        msg.obj=btn;
        //MsgHandle.sendMessage(msg);
    }
    protected void StopAutoClose()
    {
        //MsgHandle.sendEmptyMessage(2);
    }

    //判断Activity在当前激活状态
    protected static boolean isTopActivity(Activity activity) {
        String packageName =activity.getPackageName();
        ActivityManager activityManager = (ActivityManager) activity
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (activity.getClass().getName().equals(tasksInfo.get(0).topActivity.getClassName())) {
                return true;
            }
            /*if (packageName.equals(tasksInfo.get(0).topActivity
                    .getPackageName())) {
                return true;
            }*/
        }
        return false;
    }

    public void RequestPermission(String pname, final IAsynListener callback)
    {
        callBackListener=callback;
        /// Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this,pname) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{pname}, 0);
        }else{

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);

    }
    public boolean CheckPermission(String pname)
    {
        return  ContextCompat.checkSelfPermission(this,pname) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length >0 ) {
                    List<RequestPermissionsResult> permissionsResultList=new ArrayList<RequestPermissionsResult>();
                    RequestPermissionsResult res=new RequestPermissionsResult();
                    for(int i=0;i<permissions.length;i++) {
                        res.setRequestRes(grantResults[i]);
                        res.setPermissionsName(permissions[i]);
                        permissionsResultList.add(res);
                    }
                    if (callBackListener!=null) {
                        callBackListener.onFinish(this, permissionsResultList);
                    }
                } else {
                    if (callBackListener!=null) {
                        callBackListener.onFinish(this,null);
                    }
                }
                callBackListener=null;
                return;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callBackListener!=null) {
            try {
                ActivityResult res = new ActivityResult();
                res.setRequestCode(requestCode);
                res.setResultCode(resultCode);
                res.setData(data);
                callBackListener.onFinish(this,res);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        callBackListener=null;

        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onPause() {
        StopAutoClose();
        Log.d("Activity 休眠",getClass().getName());

        super.onPause();
    }

    //在Activity显示的时候，我们让NFC前台调度系统处于打开状态
    @Override
    protected void onResume() {
        Log.d("Activity 启动",getClass().getName());
        //Msgbox.hideBottomUIMenu(this.getWindow());
        hideBottomUIMenu(this.getWindow());
        try {
            /*if (PublicDefine.enjoyCard!=null) {
                PublicDefine.enjoyCard.SetIntent(this, getIntent());
                if (PublicDefine.enjoyCard.ExistsCard()) {
                    if (this instanceof IBrushCardEvent) {
                        ((IBrushCardEvent) this).BrushIn(getIntent());
                    }
                }
                else
                {
                    if (this instanceof IBrushCardEvent) {
                        ((IBrushCardEvent) this).BrushOut(getIntent());
                    }
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left ,android.R.anim.slide_out_right);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPressed();
                finish();
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    protected void StartActivityForResult(Class<?> cls, IAsynListener callback, JSONObject jsonobj) {
        callBackListener = callback;
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (jsonobj != null) {
            intent.putExtra("request", jsonobj.toString());
        }
        startActivityForResult(intent,0);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_from_left);
    }
    protected void StartActivityForResult(Class<?> cls, IAsynListener callback) {
        StartActivityForResult(cls,callback,null);
    }

    protected void StartActivityForResult(Class<?> cls, JSONObject jsonobj) {
        StartActivityForResult(cls,null,jsonobj);
    }

    protected void StartActivityForResult(Class<?> cls)
    {
        StartActivityForResult(cls,null,null);
    }

    protected String GetExceptionMsg(Throwable e)
    {
        if (e.getCause()!=null)
        {
            return GetExceptionMsg(e.getCause());
        }
        else
        {
            return e.getMessage();
        }
    }



}
