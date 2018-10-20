package ViewBind;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

/**
 * Created by 王彦鹏 on 2018-03-20.
 */

public class TextBind {
    public TextBind(String text)
    {
        Text.set(text);
        Color.set(android.graphics.Color.BLACK);
    }
    /**
     * 是否启用
     */
    public final ObservableBoolean Enabled=new ObservableBoolean(true);
    /**
     * 文本
     */
    public final ObservableField<String> Text=new ObservableField<String>("");
    /**
     * 颜色
     */
    public final ObservableInt Color=new ObservableInt(0);
    /**
     * 是否显示
     */
    public final ObservableInt Visible=new ObservableInt(View.VISIBLE);
}
