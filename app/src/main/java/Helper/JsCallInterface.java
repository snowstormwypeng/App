package Helper;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONObject;

import java.util.List;

import DataAccess.SqlLiteDataAccess;
import Enums.MsgType;
import Listener.IAsynListener;


/**
 * Created by 王彦鹏 on 2017-08-25.
 */


public class JsCallInterface {
    public String mCallJsName="";
    private Context mContext;
    public static  int webViewCount=0;
    private Handler mHandler = new Handler();
    private WebView mwebview;


    /**
     * 卡移入事件回调接口
     */
    public String CallCardInName="";

    /**
     * 消息传递
     */
    public String CallMsg="";


    public void CallJs(final String MName, final String Param)
    {
        if (MName!="") {
            System.out.println("CallName:" + MName + "  Param:" + Param);
            mHandler.post(new Runnable() {
                public void run() {
                    mwebview.loadUrl("javascript:" + MName + "('" + Param + "' );");
                }
            });
        }
        else
        {
            System.out.println("未产生Js回调，接口不存在");
        }
    }

    public JsCallInterface(Context ctx, WebView view)
    {
        mContext=ctx;
        mwebview=view;

    }

    @JavascriptInterface
    public int deleterow()
    {
        Log.d("删除行","删除行");
        return 0;
    }

    @JavascriptInterface
    public int deleteTable(final String tab)
    {
         Msgbox.Show(mContext, "清除表数据", String.format("确定要清除表【%s】的所有数据？", tab), MsgType.msg_Query, new IAsynListener() {
            @Override
            public void onFinish(Object sender, Object data) {
                SqlLiteDataAccess sqllite=new SqlLiteDataAccess(mContext);
                sqllite.ExecSql("delete from "+tab);
            }

            @Override
            public void onError(Object sender, Exception e) {

            }
        },null);
        return 0;
    }
    @JavascriptInterface
    public String getTable(String tab) throws Exception {
        SqlLiteDataAccess sqllite=new SqlLiteDataAccess(mContext);
        String htmstr="   <table class=\"gridtable\">\n" +
                "    <tr>\n" ;
        List<JSONObject> table= sqllite.Select("select * from "+tab);
        if (table.size()>0) {
            for(int i=0;i<table.get(0).names().length();i++) {
                htmstr+= "<th>"+table.get(0).names().get(i).toString()+"</th>" ;
            }
            htmstr+="<th>操作</th>  </tr>\n" ;

            for (JSONObject json : table) {
                htmstr+="    <tr>\n" ;
                for(int i=0;i<json.names().length();i++) {
                    htmstr+= "<td>"+json.getString(json.names().get(i).toString())+"</td>" ;
                }
                htmstr+="<td><a href=\"#\" onclick=\"return delrow(this);\">删除</a></td>\n" ;
                htmstr+="    </tr>\n" ;
            }
            htmstr+="    </table>\n" ;
            htmstr+="    <div style=\"margin-top: 20px;float: right;\">\n" +
                    "    <button onclick=\"return deltable('"+tab+"');\">清除此表</button>\n" +
                    "    </div>\n" ;
            return htmstr;
        }
        return "此表无数据可查";
    }


}
