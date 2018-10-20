package enjoy.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by 王彦鹏 on 2017-12-27.
 */

public class StartFilter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("启动启动", "BootReceiver.onReceive: " + intent.getAction());
        //Toast.makeText(context, "自启动程序即将执行", Toast.LENGTH_SHORT).show();
        Intent mBootIntent = new Intent(context, MainActivity.class);
        //下面这句话必须加上才能开机自动运行app的界面
        mBootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mBootIntent);
    }
}
