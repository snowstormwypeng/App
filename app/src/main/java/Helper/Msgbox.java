package Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import Enums.MsgType;
import Listener.IAsynListener;
import enjoy.ViewModel.MsgBox_ViewModel;
import enjoy.app.databinding.AlertDialogBinding;

/**
 * 弹出框
 * Created by 王彦鹏 on 2017-09-04.
 */
public class Msgbox {
    private static AlertDialog tipDialog;

    public static boolean Show(final Context ctx, String title, String message,
                               MsgType msgType, final IAsynListener OKcallback,
                               final IAsynListener Cancelcallback) {

        CloseTipDialog();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        AlertDialogBinding msgbinding = AlertDialogBinding.inflate(inflater);
        tipDialog= ShowDialog(ctx,msgbinding.getRoot());
        MsgBox_ViewModel msg_viewModel = new MsgBox_ViewModel(ctx,tipDialog,msgType,message,OKcallback,Cancelcallback);
        msgbinding.setViewModel(msg_viewModel);

        Window window =tipDialog.getWindow();
        //获得窗体的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = 750;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = 20;//设置Dialog距离底部的距离
        window.setAttributes(lp);
        return true;
    }
    public static boolean Show(final Context ctx, String message,
                               MsgType msgType, final IAsynListener OKcallback) {
        return Show(ctx,"提示",message,msgType,OKcallback,null);
    }
    public static boolean Show(Context ctx, String title, String message, MsgType msgType) {
        return Show(ctx,title, message,msgType, null,null);
    }
    public static boolean Show(Context ctx, String message) {
        return Show(ctx,"提示" ,message,MsgType.msg_Hint, null,null);
    }

    public static boolean CloseTipDialog(){
        if (tipDialog != null) {
            if (tipDialog.isShowing()) {
                tipDialog.dismiss();
            }
        }
        tipDialog=null;
        return true;
    }

    public static void hideBottomUIMenu(final Window window) {
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
    public  static AlertDialog ShowDialog(Context ctx, View view)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();

        alertDialog.show();
        Window window =alertDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        hideBottomUIMenu(window);
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        // 去除四角黑色背景
        window.setBackgroundDrawable(new BitmapDrawable());
        // 设置周围的暗色系数
        params.dimAmount = 0.5f;

        window.setAttributes(params);
        window.setContentView(view);

        return alertDialog;
    }

}
