package ViewModel;

import android.content.Context;
import android.view.View;

import ViewBind.ButtonBind;
import ViewBind.TextBind;

/**
 * 作者：王彦鹏 on 2018-11-02 09:33
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class DbManage_ViewModel extends Base_ViewModel {
    public final TextBind Title=new TextBind("");
    public final ButtonBind Btn_Close=new ButtonBind("关闭");
    public  final ButtonBind Btn_Cancel=new ButtonBind("取消");
    public final ButtonBind Btn_OK=new ButtonBind("");


    public DbManage_ViewModel(Context base) {
        super(base);
    }

    public void Btn_Close_Click(View view)
    {

    }
    public void Btn_OK_Click(View view)
    {

    }
}
