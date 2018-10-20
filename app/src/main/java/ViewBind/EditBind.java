package ViewBind;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;


/**
 * Created by 王彦鹏 on 2018-03-20.
 */

public class EditBind {
    public EditBind(String value)
    {
        TextValue.set(value);
    }
    /**
     * 是否启用
     */
    public final ObservableBoolean Enabled=new ObservableBoolean(true);
    /**
     * 输入框值
     */
    public final ObservableField<String> TextValue=new ObservableField<String>("");
    /**
     * 输入框值
     */
    public final ObservableInt IntValue=new ObservableInt(0);
}
