package Enums;

import Annotation.Description;

/**
 * Created by 王彦鹏 on 2018-01-08.
 */

public enum NetworkType_Enum {
    @Description("未使用")
    Network_Null,
    @Description("以太网")
    Network_Ethernet,
    @Description("Wifi")
    Network_Wifi,
    @Description("移动网络")
    Network_Mobile
}
