package enjoy.Filter;

import android.content.Context;

import java.lang.reflect.Method;

import Filter.IFilter;
import ViewModel.Base_ViewModel;
import enjoy.ViewModel.MainActivity_ViewModel;

/**
 * 作者：王彦鹏 on 2018-10-22 15:56
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class IAopDemo_Filter2 implements IFilter {
    private Base_ViewModel viewModel;
    public IAopDemo_Filter2(Context viewModel)
    {
        this.viewModel=(Base_ViewModel)viewModel;
    }
    @Override
    public Object Before(Object sender, Method method, Object[] args) throws Exception {
        String info=((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.get();
        ((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.set(info+"\r\n"+
                String.format("%s\r\n%s.Before->第一个参数：%s；第二个参数：%s","第二个拦截器：", method.getName(),args[0],args[1]));
        return null;
    }

    @Override
    public Object After(Object sender, Method method, Object[] args, Object retvalue) throws Exception {
        String info=((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.get();
        ((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.set(info+"\r\n"+
                String.format("%s\r\n%s.After->第一个参数：%s；第二个参数：%s；原接口返回数据：%s","第二个拦截器改变返回值为“测试”：",method.getName(),args[0],args[1],retvalue));
        return "测试";
    }
}
