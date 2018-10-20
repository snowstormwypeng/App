package Listener;

import Enums.NetworkType_Enum;

/**
 * Created by 王彦鹏 on 2018-01-08.
 */

public interface INetworkListener  {
    /**
     * 网络状态变化事件
     * @param NetworkType
     * @param ConnState
     */
    void NetworkStateEvent(NetworkType_Enum NetworkType, boolean ConnState);
}
