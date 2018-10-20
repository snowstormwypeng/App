package enjoy.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import Enums.NetworkType_Enum;
import Listener.INetworkListener;

/**
 * Created by 王彦鹏 on 2018-01-08.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkBroad";
    private Context ctx=null;


    public NetworkConnectChangedReceiver(Context ctx)
    {
        this.ctx=ctx;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,context.getClass().getName());

        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Log.i(TAG, "CONNECTIVITY_ACTION");
            Context observer=(ctx==null? context:ctx);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet

                switch (activeNetwork.getType()) {
                    case ConnectivityManager.TYPE_WIFI: {
                        if (observer instanceof INetworkListener) {
                            ((INetworkListener) observer).NetworkStateEvent(NetworkType_Enum.Network_Wifi, activeNetwork.isConnected());
                        }
                        break;
                    }
                    case ConnectivityManager.TYPE_ETHERNET: {
                        if (observer instanceof INetworkListener) {
                            ((INetworkListener) observer).NetworkStateEvent(NetworkType_Enum.Network_Ethernet,activeNetwork.isConnected());
                        }
                        break;
                    }
                    case ConnectivityManager.TYPE_MOBILE: {
                        if (observer instanceof INetworkListener) {
                            ((INetworkListener) observer).NetworkStateEvent(NetworkType_Enum.Network_Mobile, activeNetwork.isConnected());
                        }
                        break;
                    }
                    default: {
                        if (observer instanceof INetworkListener) {
                            ((INetworkListener) observer).NetworkStateEvent(NetworkType_Enum.Network_Null,activeNetwork.isConnected());
                        }
                        break;
                    }
                }
            }
            else {   // not connected to the internet
                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                if (observer instanceof INetworkListener)
                {
                    ((INetworkListener)observer).NetworkStateEvent(NetworkType_Enum.Network_Wifi,false);
                }
            }
        }
    }
}
