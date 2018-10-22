package ViewModel;

import android.content.Context;
import android.view.View;

import Enums.MsgType;
import Helper.Msgbox;
import Listener.IAsynListener;
import ViewBind.ButtonBind;
import ViewBind.TextBind;

/**
 * Created by 王彦鹏 on 2017/9/28.
 */

public class MainActivity_ViewModel extends Base_ViewModel {
    public MainActivity_ViewModel(Context base) {
        super(base);
    }

    public final TextBind Text_Info=new TextBind("第一页");
    public final ButtonBind Btn_Msg=new ButtonBind("提示框");

    public void Btn_Msg_OnClick(View view)
    {
        Msgbox.Show(getBaseContext(),  "这是一个弹出框测试", MsgType.msg_Query, new IAsynListener() {
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
}
