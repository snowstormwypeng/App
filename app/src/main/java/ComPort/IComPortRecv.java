package ComPort;

/**
 * 串口数据回调事件
 * Created by 王彦鹏 on 2018-01-31.
 */

public interface IComPortRecv {
    /**
     * 串口数据回调
     * @param data
     * @param size
     */
    void RecvData(byte[] data, int size);
}
