package enjoy.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ScrollView;

import Enums.MsgType;
import Factory.Factory;
import Helper.Msgbox;
import Listener.IAsynListener;
import ViewBind.ButtonBind;
import ViewBind.TextBind;
import ViewModel.Base_ViewModel;
import enjoy.Interface.IAopDemo;
import enjoy.InterfaceImpl.AopDemo;
import enjoy.app.R;

/**
 * 作者：王彦鹏 on 2018-10-22 15:27
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class MainActivity_ViewModel extends Base_ViewModel {
    private ScrollView scrollView;
    IAopDemo demo=Factory.GetInstance(AopDemo.class,new Object[]{this});
    public MainActivity_ViewModel(Context base) {
        super(base);
    }


    public final TextBind Text_Info=new TextBind("第一页");
    public final TextBind Text_AopInfo=new TextBind("");
    public final ButtonBind Btn_Msg=new ButtonBind("提示框");
    public final ButtonBind Btn_Aop=new ButtonBind("AopDemo");
    public final ButtonBind Btn_Clear=new ButtonBind("清除");


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
}
