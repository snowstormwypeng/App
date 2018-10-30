package ViewBind;

import android.databinding.BindingAdapter;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 作者：王彦鹏 on 2018-10-30 16:40
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class ImageBind {
    public ImageBind(int resId)
    {
        //ResId.set(resId);
    }
    @BindingAdapter("android:src")
    public void setSrc(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter("android:src")
    public void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }

}
