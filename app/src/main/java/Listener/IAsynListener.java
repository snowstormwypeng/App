package Listener;

import Interface.IInterface;

/**
 * Created by 王彦鹏 on 2017-11-17.
 */

public interface IAsynListener extends IInterface {
    /**
     * 数据响应成功
     *
     * @param data 调度者
     */
    void onFinish(Object sender, Object data);

    /**
     * 数据请求失败
     * @param sender  调度者
     * @param e 异常对象
     */

    void onError(Object sender, Exception e);
}
