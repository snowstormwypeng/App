package enjoy.Filter;

import android.content.Context;

import java.lang.reflect.Method;

import Filter.IFilter;
import ViewModel.Base_ViewModel;
import enjoy.ViewModel.MainActivity_ViewModel;

/**
 * 作者：王彦鹏 on 2018-10-22 15:51
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class IAopDemo_Filter implements IFilter {
    private Base_ViewModel viewModel;
    /**
     * 如果被注入的对象有构造参数，寻么拦截器也要有同样的构造参数
     * @param viewModel
     */
    public IAopDemo_Filter(Context viewModel)
    {
        this.viewModel=(Base_ViewModel)viewModel;
    }
    @Override
    public Object Before(Object sender, Method method, Object[] args) throws Exception {
        String info=((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.get();
        ((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.set(info+"\r\n"+
                String.format("%s\r\n%s.Before->第一个参数：%s；第二个参数：%s","第一个拦截器：", method.getName(),args[0],args[1]));
        return null;
    }

    @Override
    public Object After(Object sender, Method method, Object[] args, Object retvalue) throws Exception {
        String info=((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.get();
        ((MainActivity_ViewModel)viewModel).Text_AopInfo.Text.set(info+"\r\n"+
                String.format("%s\r\n%s.After->第一个参数：%s；第二个参数：%s；原接口返回数据：%s","第一个拦截器不改变返回值：",method.getName(),args[0],args[1],retvalue));
        return null;
    }
}
