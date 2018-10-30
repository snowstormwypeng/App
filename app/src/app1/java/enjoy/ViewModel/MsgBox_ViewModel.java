package enjoy.ViewModel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Observable;
import android.view.View;
import android.widget.ImageView;

import java.security.PublicKey;

import Enums.MsgType;
import Helper.PhoneSound;
import Listener.IAsynListener;
import ViewBind.ButtonBind;
import ViewBind.ImageBind;
import ViewBind.TextBind;
import ViewModel.Base_ViewModel;
import enjoy.app.R;


/**
 * 作者：王彦鹏 on 2018-10-27 12:04
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class MsgBox_ViewModel extends Base_ViewModel {
    private MsgType msgType;
    private Dialog dialog;
    private  IAsynListener OKcallback, Cancelcallback;
    public MsgBox_ViewModel(Context base, Dialog dialog, MsgType msgType, String msg,final IAsynListener OKcallback,
                            final IAsynListener Cancelcallback) {
        super(base);
        this.dialog=dialog;
        Info.Text.set(msg);
        this.msgType=msgType;
        this.OKcallback=OKcallback;
        this.Cancelcallback=Cancelcallback;
        ImageView img=dialog.findViewById(R.id.msg_img);

        switch (msgType)
        {
            case msg_Error:
            {
                Btn_Cancel.Visible.set(View.GONE);
                img.setImageDrawable(getResources().getDrawable(R.drawable.icon_error));
                PhoneSound.play(base,R.raw.hint_2);
                break;
            }
            case msg_Succeed:
            {
                Btn_Cancel.Visible.set(View.GONE);
                img.setImageDrawable(getResources().getDrawable(R.drawable.icon_success));
                PhoneSound.play(base,R.raw.hint_8);
                break;
            }
            case msg_Input:
            {
                Btn_Cancel.Visible.set(View.GONE);
                PhoneSound.play(base,R.raw.hint_10);

                break;
            }
            case msg_warning: {
                PhoneSound.play(base, R.raw.hint_3);
                Btn_Cancel.Visible.set(View.GONE);
                img.setImageDrawable(getResources().getDrawable(R.drawable.icon_warning));
                break;
            }
            case msg_Query:
            {
                Btn_Cancel.Visible.set(View.VISIBLE);
                img.setImageDrawable(getResources().getDrawable(R.drawable.icon_help));
                PhoneSound.play(base,R.raw.hint_8);
                //btncancel.setVisibility(View.VISIBLE);
                break;
            }
            default:{
                Btn_Cancel.Visible.set(View.GONE);
                img.setImageDrawable(getResources().getDrawable(R.drawable.icon_hint));
                PhoneSound.play(base,R.raw.hint_1);
                break;
            }
        }
    }
    public final TextBind Title=new TextBind("提示");
    public final ButtonBind Btn_Close=new ButtonBind("×");
    public final TextBind Info=new TextBind("");
    public final ButtonBind Btn_Cancel=new ButtonBind("取消");
    public final ButtonBind Btn_OK=new ButtonBind("确定");
    public final ImageBind Img=new ImageBind(0);


    public void Btn_Close_Click(View view)
    {
        dialog.dismiss();
        if (Cancelcallback!=null)
        {
            Cancelcallback.onFinish(getBaseContext(),this);
        }
    }
    public void Btn_OK_Click(View view)
    {
        dialog.dismiss();
        if (OKcallback!=null)
        {
            OKcallback.onFinish(getBaseContext(),this);
        }
    }
}
