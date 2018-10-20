package ViewBind;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

/**
 * Created by 王彦鹏 on 2018-03-20.
 */

public class ButtonBind extends TextBind {

    public ButtonBind(String text) {
        super(text);
        Text.set(text);
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
     * 是否显示
     */
    public final ObservableInt Visible=new ObservableInt(View.VISIBLE);
}
