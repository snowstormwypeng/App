package DataAccess;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;


/**
 * Created by 王彦鹏 on 2017-09-02.
 */

public class OptionDataAccess extends DataAccess {


    /**
     * 创建或打开数据库
     *
     * @param context
     */
    public OptionDataAccess(Context context) {
        super(context);
    }

    public boolean SetOption(String setName, String colName, Object value)
    {
        String sql="";
        JSONObject row=GetOption(setName);
        if (row==null) {
            sql= String.format("insert INTO SystemOption(setname,%s) values('%s','%s')",
                    colName,setName, value);
        }
        else {
            sql = String.format("update SystemOption set %s='%s' where setname='%s'",
                    colName, value, setName);
        }
        return ExecSql(sql);
    }

    public  boolean Delete(String name)
    {
        String sql="delete from SystemOption where setname ='"+name+"'";
        return ExecSql(sql);
    }

    public JSONObject GetOption(String setName)
    {
        try {
            String sql = "select * from SystemOption where setname ='"+setName+"'" ;
            List<JSONObject> list = Select(sql);
            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
