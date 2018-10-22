package enjoy.Interface;

import Annotation.Filter;
import Interface.IInterface;
import enjoy.Filter.IAopDemo_Filter;
import enjoy.Filter.IAopDemo_Filter2;

/**
 * 作者：王彦鹏 on 2018-10-22 15:49
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public interface IAopDemo extends IInterface {
    /**
     * 一个Aop测试
     * @param p1 第一个参数
     * @param p2 第二个参数
     * @return 返回信息
     */
    @Filter({IAopDemo_Filter.class,IAopDemo_Filter2.class})
    String GetData(String p1,String p2);
}
