package ComPort;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Helper.EnjoyTools;
import Helper.Log;
import Listener.IAsynListener;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * Created by 王彦鹏 on 2018-01-31.
 */

public class ComDevice extends ContextWrapper {
    private SerialPort mSerialPort=null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private byte[] buffer=new byte[128];
    private int bufferSize=0;
    private Object syncobj = new Object();
    private boolean[] recvFlag=new boolean[]{false};
    private String comName="";
    private Context ctx;
    private ExecutorService executor;
    /**
     * 收到数据的处理事件
     */
    private IAsynListener RecvCallBack;

    public List<String> ComDevicesList = new ArrayList<String>();

    class callTAsk implements Callable<Integer> {
        private  byte[] buf;

        public callTAsk( byte[] data)
        {
            buf=data;
        }

        @Override
        public Integer call() throws Exception {
            recvFlag[0] = true;
            if (RecvCallBack!=null)
            {
                Log.write(comName, "收到数据执行回调");
                RecvCallBack.onFinish(buf,buf.length);
                Log.write(comName, "接收回调清除");
                //RecvCallBack=null;
            }
            else
            {
                Log.write(comName, "收到数据但未发现回调地址");
            }
            if (ctx instanceof IComPortRecv) {
                ((IComPortRecv) ctx).RecvData(buf, buf.length);
            }
            return 0;
        }
    };

        private Runnable mRunnable = new Runnable() {
        public void run() {
            Object obj=new Object();
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while (!executor.isShutdown()) {
                try {
                    synchronized (syncobj) {
                        int size; //读取数据的大小
                        if (inputStream != null) {
                            size = inputStream.available();
                            if (size > 0) {
                                size = inputStream.read(buffer);
                                byte[] buf = new byte[size];
                                bufferSize=size;
                                System.arraycopy(buffer, 0, buf, 0, size);
                                Log.write(comName, String.format(" %s 收到数据：%s", comName, EnjoyTools.ByteArrayToHexString(buf)));
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.submit(new callTAsk(buf));//将任务提交给线程池
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    synchronized (obj)
                    {
                        obj.wait(10);
                    }

                } catch (Exception e) {

                }
                /*if (inputStream != null) {
                    mHandler.postDelayed(mRunnable, 10);  //给自己发送消息，自运行
                }*/
            }
            Log.write(comName, String.format(" %s 关闭", comName));
        }
    };
    public ComDevice(Context base)  {
        super(base);
        ctx=this;

        SerialPortFinder serialPortFinder=new SerialPortFinder();
        String[] entryValues = serialPortFinder.getAllDevicesPath();

        for (int i = 0; i < entryValues.length; i++) {
            ComDevicesList.add(entryValues[i]);
        };

    }

    public void destroy()
    {
        Log.write(comName, String.format(" %s 准备退出",comName));
        executor.shutdown();
        Close();
        Log.write(comName, String.format(" %s 已退出",comName));
    }

    /**
     * 打开串口
     */
    public void Open(final String comport, final int baudrate, final int databits,
                     final int stopbits, final char parity) throws Exception {
        comName = comport;
        mSerialPort = new SerialPort(new File(comport), baudrate, databits, stopbits, parity);
        //获取打开的串口中的输入输出流，以便于串口数据的收发
        inputStream = mSerialPort.getInputStream();
        outputStream = mSerialPort.getOutputStream();
        Log.write(comName, String.format("打开串口:%s", comport));
        executor = Executors.newSingleThreadExecutor();
        executor.execute(mRunnable);
    }

    /**
     * 关闭串口
     */
    public void Close() {
        try {
            mSerialPort.close();
            inputStream.close();
            outputStream.close();
            inputStream=null;
            outputStream=null;
            mSerialPort.close();
            Log.write(comName, String.format("串口 %s 已关闭", comName));
        } catch (IOException e) {
            Log.write(comName, String.format("关闭串口 %s 异常：%s", comName, e.toString()));
            return;
        }

    }

    /**
     * 发送数据
     *
     * @param data
     * @return 返回发送的数据长度
     */
    public int SendData(byte[] data) {
        synchronized (syncobj) {
            recvFlag[0] = false;
            if (outputStream != null) {
                Log.write(comName, String.format("%s 发送数据:%s", comName, EnjoyTools.ByteArrayToHexString(data)));
                try {
                    outputStream.write(data);
                } catch (IOException e) {
                    Log.write(comName, String.format("%s 发送失败：", comName, e.toString()));
                }
                return data.length;
            } else {
                return 0;
            }
        }
    }


    /**
     * 发送数据
     * @param data 发送的数据
     * @param recvCallBack 收到数据后的事件处理
     */
    public void SendData(byte[] data,IAsynListener recvCallBack) {
        synchronized (syncobj) {
            Log.write(comName, "开始发送数据");
            if (outputStream != null) {
                RecvCallBack=recvCallBack;
                Log.write(comName, String.format("%s 发送数据:%s", comName, EnjoyTools.ByteArrayToHexString(data)));
                try {
                    outputStream.write(data);
                } catch (IOException e) {
                    Log.write(comName, String.format("%s 发送失败：", comName, e.toString()));
                }
            } else {
                recvCallBack.onError(this,new Exception("设备未连接"));
            }
        }
        Log.write(comName, "数据发送完毕");
    }

}


