package ViewModel;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Entity.RequestPermissionsResult;
import Listener.IAsynListener;
import Listener.INetworkListener;
import enjoy.app.BuildConfig;
import enjoy.app.NetworkConnectChangedReceiver;
import enjoy.app.R;

/**
 * Created by 王彦鹏 on 2018-03-16.
 */

public class Base_ViewModel extends ContextWrapper {
    private IAsynListener callBackListener;
    private static Stack<Activity> activityStack;

    protected boolean CheckPermission(String pname)
    {
        return  ContextCompat.checkSelfPermission(this,pname) == PackageManager.PERMISSION_GRANTED;
    }

    protected void RequestPermission(String pname, final IAsynListener callback)
    {
        callBackListener=callback;
        /// Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this,pname) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)getBaseContext(), new String[]{pname}, 0);

        }else{

        }
    }

    /**
     * Activeti 关闭回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                              @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length >0 ) {
                    if (grantResults[0] == 0){
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
                    }else{
                        callBackListener.onError(this,null);
                    }
                } else {
                    if (callBackListener!=null) {
                        callBackListener.onError(this,null);
                    }
                }
                callBackListener=null;
                return;
            }
        }
    }

    protected void StartActivityForResult(Class<?> cls, IAsynListener callback, JSONObject jsonobj) {
        callBackListener = callback;
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (jsonobj != null) {
            intent.putExtra("request", jsonobj.toString());
        }
        ((Activity)getBaseContext()).startActivityForResult(intent,0);
        ((Activity)getBaseContext()).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_from_left);

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

    public Base_ViewModel(Context base) {
        super(base);

        //全屏显示
        View decorView = ((Activity)base).getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        View view= ((Activity)getBaseContext()).findViewById(android.R.id.content);
        view.setOnLongClickListener(new OnLongClickListenerImpl());

        if (this instanceof INetworkListener) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            filter.addAction("android.net.wifi.STATE_CHANGE");

            NetworkConnectChangedReceiver networkConnectChangedReceiver = new NetworkConnectChangedReceiver(this);
            getApplicationContext().registerReceiver(networkConnectChangedReceiver, filter);

        }


    }

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj instanceof IAsynListener) {
                ((IAsynListener) msg.obj).onFinish(Base_ViewModel.this, msg.arg1);
            }
        }
    };

    //获取异常原因
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

    //判断Activity在当前激活状态
    protected static boolean isTopActivity(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (activity.getClass().getName().equals(tasksInfo.get(0).topActivity.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //退出栈中所有Activity
    public void popAllActivity(){
        while(true){
            Activity activity=currentActivity();
            if(activity==null){
                break;
            }
            popActivity(activity);
        }
    }

    //将当前Activity推入栈中
    public void pushActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    //获得当前栈顶Activity
    public Activity currentActivity(){
        Activity activity=activityStack.lastElement();
        return activity;
    }

    //退出栈顶Activity
    public void popActivity(Activity activity){
        if(activity!=null){
            activity.finish();
            activityStack.remove(activity);
            activity=null;
        }
    }

    public void alert(final String info){
        Message msg = new Message();
        msg.obj = new IAsynListener() {
            @Override
            public void onFinish(Object sender, Object data) {
                Toast toast=Toast.makeText(getBaseContext(),info, Toast.LENGTH_LONG);
                LayoutInflater inflate = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //View view = inflate.inflate(R.layout.custom_alertdialog, null);
                //view.findViewById(R.id.dialogclose).setVisibility(View.GONE);

                //TextView tv=(TextView)view.findViewById(R.id.tv_dialog_message);
                //tv.setText(info);
                //tv=(TextView)view.findViewById(R.id.tv_dialog_title);
                //tv.setText("提示信息");
//
//                ImageView image= (ImageView) view.findViewById(R.id.id_image);
//                image.setImageResource(R.drawable.icon_hint);
                //toast.setView(view);
                toast.setGravity(Gravity.CENTER,0,50);
                toast.show();

            }

            @Override
            public void onError(Object sender, Exception e) {

            }
        };
        mHandler.sendMessage(msg);
    }

    public class OnLongClickListenerImpl implements View.OnLongClickListener {

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

    public void Init()
    {

    }

    public void Destroy()
    {

    }
}
