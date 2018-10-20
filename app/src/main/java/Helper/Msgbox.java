package Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import Enums.MsgType;
import Listener.IAsynListener;
import enjoy.app.R;

/**
 * 弹出框
 * Created by 王彦鹏 on 2017-09-04.
 */
public class Msgbox {
    private static AlertDialog tipDialog;

    private static class CustomAdapter extends BaseAdapter {

        private List<ItemBean> items;
        private LayoutInflater inflater;
        private ImageView image;
        private TextView text;

        public CustomAdapter(List<ItemBean> items, Context context) {
            this.items = items;
            this.inflater = LayoutInflater.from(context);
        }



        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){

                view=inflater.inflate(R.layout.custom_adapter,null);
                image= (ImageView) view.findViewById(R.id.id_image);
                text= (TextView) view.findViewById(R.id.id_text);
            }
            image.setImageResource(items.get(i).getImageId());
            text.setText(items.get(i).getMessage());
            return view;
        }
    }
    private static class ItemBean{
        private int imageId;
        private String message;

        public ItemBean(int imageId, String message) {
            this.imageId = imageId;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static boolean JSPrompt(Context ctx, String message,
                                   String defaultValue, final JsPromptResult result) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("对话框").setMessage(message);
        final EditText et = new EditText(ctx);
        et.setSingleLine();
        et.setText(defaultValue);
        builder.setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (result!=null) {
                            result.confirm(et.getText().toString());
                        }
                    }

                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (result!=null) {
                            result.cancel();
                        }
                    }
                });

        // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                return true;
            }
        });

        // 禁止响应按back键的事件
        // builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        return true;
        // return super.onJsPrompt(view, url, message, defaultValue,
        // result);
    }

    public static boolean JSConfirm(Context ctx, String message, final JsResult result) {
        new AlertDialog.Builder(ctx)
                .setTitle("询问")
                .setIcon(android.R.drawable.ic_menu_help)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (result!=null) {
                            result.confirm();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (result!=null) {
                            result.cancel();
                        }
                    }
                })
                .setCancelable(false)
                .create().show();
        //result.confirm();
        return true;
    }

    public static boolean JSAlert(Context ctx, String message, final JsResult result) {
        new AlertDialog.Builder(ctx)
                .setTitle("提示")
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (result!=null) {
                            result.confirm();
                        }
                    }
                })
                .setCancelable(false)
                .create().show();
        //result.confirm();
        return true;
    }
    public static boolean Show(final Context ctx, String title, String message,
                               MsgType msgType, final IAsynListener OKcallback,
                               final IAsynListener Cancelcallback) {

        CloseTipDialog();
        tipDialog= ShowDialog(ctx, R.layout.alert_dialog);
        //获取当前Activity所在的窗体
        Window dialogWindow = tipDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = 700;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = 20;//设置Dialog距离底部的距离
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        ((TextView)tipDialog.findViewById(R.id.alertText)).setText(message);
        ((TextView)tipDialog.findViewById(R.id.tv_dialog_title)).setText(title);
        Button btncancel = (Button) tipDialog.findViewById(R.id.btncancel);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipDialog.dismiss();
            }
        });
        Button btnsuccess = (Button) tipDialog.findViewById(R.id.btnsuccess);
        btnsuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (OKcallback!=null) {
                        tipDialog.dismiss();
                        OKcallback.onFinish(null,"");
                    }else{
                        tipDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (Cancelcallback!=null)
                {
                    Cancelcallback.onFinish(null,ctx);
                }
            }
        });
        TextView close = (TextView) tipDialog.findViewById(R.id.dialogclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipDialog.dismiss();
            }
        });

        switch (msgType)
        {
            case msg_Error:
            {
                PhoneSound.play(ctx, R.raw.hint_2);
                break;
            }
            case msg_Succeed:
            {
                PhoneSound.play(ctx,R.raw.hint_8);
                break;
            }
            case msg_Input:
            {
                PhoneSound.play(ctx,R.raw.hint_10);
                //items.add(new ItemBean(R.drawable.icon_hint,message));
                final EditText et = new EditText(ctx);
                et.setSingleLine();
                et.setText(message);
                break;
            }
            case msg_warning: {
                PhoneSound.play(ctx, R.raw.hint_3);
                break;
            }
            case msg_Query:
            {
                PhoneSound.play(ctx,R.raw.hint_8);
                btncancel.setVisibility(View.VISIBLE);
                break;
            }
            default:{
                PhoneSound.play(ctx,R.raw.hint_1);
                break;
            }
        }
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

    public  static void ShowDialog(Context ctx, String info, final IAsynListener callback)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.show();
        Window window =alertDialog.getWindow();
        hideBottomUIMenu(window);
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.custom_alertdialog);
        TextView tv_title = (TextView) window.findViewById(R.id.tv_dialog_title);
        tv_title.setText("更多规则");

        Button btnclose=(Button) window.findViewById(R.id.dialogclose);
        btnclose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (callback!=null) {
                    callback.onFinish(null,view);
                }
                alertDialog.dismiss();
            }
        });
        TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);

        tv_message.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_message.setText(Html.fromHtml(info));

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        // 去除四角黑色背景
        window.setBackgroundDrawable(new BitmapDrawable());
        // 设置周围的暗色系数
        params.dimAmount = 0.5f;
        window.setAttributes(params);

    }

    public  static void ShowDialog(Context ctx, String info)
    {
        ShowDialog(ctx,info,null);
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
    public  static AlertDialog ShowDialog(Context ctx, int resId)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();

        alertDialog.show();
        Window window =alertDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setContentView(resId);
        hideBottomUIMenu(window);
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        // 去除四角黑色背景
        window.setBackgroundDrawable(new BitmapDrawable());
        // 设置周围的暗色系数
        params.dimAmount = 0.5f;
        window.setAttributes(params);
        return alertDialog;
    }

}
