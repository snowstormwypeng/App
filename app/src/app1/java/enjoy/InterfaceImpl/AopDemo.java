package enjoy.InterfaceImpl;

import android.content.Context;

import ViewModel.Base_ViewModel;
import enjoy.Interface.IAopDemo;
import ViewModel.MainActivity_ViewModel;

/**
 * 作者：王彦鹏 on 2018-10-22 15:58
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class AopDemo implements IAopDemo {
    private Base_ViewModel viewModel;
    public AopDemo(Context viewModel)
    {
        this.viewModel=(Base_ViewModel)viewModel;
    }

    @Override
    public String GetData(String p1, String p2) {
        String info=((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.get();
        ((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.set(info+"\r\n原接口返回：AOPDemo");
        return "AOPDemo";
    }
}
