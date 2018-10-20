/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

/**
 *
 * @author Administrator
 */
public class ByteControl {

    public static long byte2int(byte[] src) {
        long res = 0;

        for (int i = 0; i < src.length; i++) {
            res = res | ((long) src[i] & 0xff) << (i * 8);
        }

        return res & 0xffffffff;
    }

    public static byte[] long2byte(long i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[3] = (byte) ((i >> 24) & 0xFF);
        return result;
    }
    
   
    //int转成4字节存储
    public static byte[] intTobyte4(int i)
    {
        byte[] result = new byte[4];
        result[0] = (byte) (i & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[3] = (byte) ((i >> 24) & 0xFF);
        return result;
    }
    //int转成3字节存储
    public static byte[] intTobyte3(int i)
    {
        byte[] result = new byte[3];
        result[0] = (byte) (i & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        return result;
    }
    //int转成2字节存储
    public static byte[] intTobyte2(int i)
    {
        byte[] result = new byte[2];
        result[0] = (byte) (i & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        return result;
    }
    //4字节转成long
    public static long byte4ToLong(byte[] data, int index) {
        long i = 0l;
        i = (data[3 + index] << 24 & 0x00000000ff000000l)
                + (data[2 + index] << 16 & 0x0000000000ff0000l)
                + (data[1 + index] << 8 & 0x000000000000ff00l)
                + (data[0 + index] & 0x00000000000000ffl);
        return i;
    }
    //4字节转成int
    public static int byte4Toint(byte [] data,int index)
    {
        int i = 0;
       i = (data[3 + index] << 24 & 0xff000000)
                + (data[2 + index] << 16 & 0x00ff0000) 
                + (data[1 + index]<<8 & 0x0000ff00)
                + (data[0 + index] & 0x000000ff);
        return i;
    }
    //3字节转成int
    public static int byte3Toint(byte [] data,int index)
    {
        int i = 0;
        i = (data[2 + index] << 16 & 0x00ff0000) 
                + (data[1 + index]<<8 & 0x0000ff00)
                + (data[0 + index] & 0x000000ff);
        return i;
    }
    //2字节转成int
    public static int byte2Toint(byte [] data,int index)
    {
        int i = 0;
        i = (data[1+index]<<8 & 0x0000ff00)+(data[0+index] & 0x000000ff);

        return i;
    }
    public static byte[] byteAdd(byte[] fir, byte[] sec) {
        byte[] res = new byte[fir.length + sec.length];

        for (int i = 0; i < fir.length; i++) {
            res[i] = fir[i];
        }

        for (int i = 0; i < sec.length; i++) {
            res[i + fir.length] = sec[i];
        }

        return res;
    }

    public static byte[] subByte(byte[] src, int from, int length) {
        byte[] res = new byte[length];

        for (int i = 0; i < length; i++) {
            res[i] = src[from + i];
        }

        return res;
    }       
    
    //内存拷贝
    public static void memcpy(byte[] Des,int DesIndex,byte[] Src,int SrcIndex,int CopyLenth)
    {
        int i;
        for (i=0;i<CopyLenth;i++)
        {
            Des[DesIndex+i] = Src[SrcIndex+i];
        }
    }
    
    //内存赋值
    public static void memset(byte[] Des,int DesIndex,byte Value,int SetCount)
    {
        int i;
        for (i=0;i<SetCount;i++)
        {
            Des[DesIndex+i] = Value;
        }
    }
    
    //字节流转换成16进制字符串
    public static String bArrayToString(byte [] Data)
    {
        String str = "";
        for (int i = 0; i < Data.length; i++) {
            String s = String.format("%02X",Data[i] & 0xff);
            if (s.length() == 1) {
                s = "0" + s;
            }
            str = str + s;
        }
        return str;
    }
    private static byte chartobyte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    public static byte[] HexStrToBytes(String hexstring) {
        if (hexstring == null || hexstring.equals("")) {
            return null;
        }
        char[] chararray = hexstring.toCharArray();
        int len = chararray.length / 2;
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            b[i] = (byte) (chartobyte(chararray[pos]) << 4 | chartobyte(chararray[pos + 1]));
        }
        return b;
    }
}
