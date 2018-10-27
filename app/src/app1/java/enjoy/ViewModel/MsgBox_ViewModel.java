package enjoy.ViewModel;

import android.content.Context;

import ViewBind.ButtonBind;
import ViewBind.TextBind;
import ViewModel.Base_ViewModel;

/**
 * 作者：王彦鹏 on 2018-10-27 12:04
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class MsgBox_ViewModel extends Base_ViewModel {
    public MsgBox_ViewModel(Context base) {
        super(base);
    }
    public final TextBind Title=new TextBind("提示");
    public final ButtonBind Btn_Close=new ButtonBind("×");
    public final TextBind Info=new TextBind("");
    public final ButtonBind Btn_Cancel=new ButtonBind("取消");
    public final ButtonBind Btn_OK=new ButtonBind("确定");

}
