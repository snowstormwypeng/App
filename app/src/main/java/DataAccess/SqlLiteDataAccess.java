package DataAccess;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王彦鹏 on 2018-01-19.
 */

public class SqlLiteDataAccess extends DataAccess {
    public SqlLiteDataAccess(Context context) {
        super(context);
    }

    public List<String> queryTable() throws Exception {
        String sql="SELECT * FROM sqlite_master where  type='table'";
        List<JSONObject> jobj = Select(sql);
        List<String> strs = new ArrayList<String>();
        for(int i = 1; i < jobj.size(); i++){
            strs.add(jobj.get(i).get("name").toString());
        }
        return strs;
    }
}
