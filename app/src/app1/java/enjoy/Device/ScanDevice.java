package enjoy.Device;

import android.content.Context;

import java.io.UnsupportedEncodingException;

import ComPort.ComDevice;
import ComPort.IComPortRecv;
import Listener.IAsynListener;

/**
 * 作者：王彦鹏 on 2019-08-22 09:32
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class ScanDevice extends ComDevice implements IComPortRecv {
    private IAsynListener callback;
    public ScanDevice(Context base, IAsynListener callBack) {
        super(base);
        this.callback=callBack;
        try {
            Open("/dev/ttyS1",115200,8,1,'N');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RecvData(byte[] data, int size) {
        String str= null;
        try {
            str = new String(data,"gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (callback!=null )
        {
            callback.onFinish(this,str);
        }
    }
}
