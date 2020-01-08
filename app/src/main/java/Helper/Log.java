package Helper;

/**
 * 日志对象
 * Created by 王彦鹏 on 2018-03-29.
 */
public class Log {
    /**
     * 写日志
     * @param tag 标签
     * @param log 日志内容
     */
    public static void write(String tag, String log)
    {
        android.util.Log.d(tag,log);
    }
    public static void d(String tag, String log)
    {
        write(tag,log);
    }
}
