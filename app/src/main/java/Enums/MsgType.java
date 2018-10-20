package Enums;

import Annotation.Description;

/**
 * Created by 王彦鹏 on 2017-09-23.
 */

public enum MsgType {
    @Description("普通提示框")
    msg_Hint,
    @Description("警告提示框")
    msg_warning,
    @Description("询问提示框")
    msg_Query,
    @Description("输入对话框")
    msg_Input,
    @Description("错误/失败提示框")
    msg_Error,
    @Description("成功提示框")
    msg_Succeed
}
