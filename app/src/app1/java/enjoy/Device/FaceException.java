package enjoy.Device;

/**
 * 作者：王彦鹏 on 2019-08-24 10:39
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class FaceException extends Exception {
    private int ecode=-1;

    public int getEcode() {
        return ecode;
    }

    public void setEcode(int ecode) {
        this.ecode = ecode;
    }
    public FaceException(int ecode,String msg)
    {
        super(msg);
        this.ecode=ecode;

    }
}
