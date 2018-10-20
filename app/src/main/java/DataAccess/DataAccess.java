package DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import Annotation.ColAttrib;
import Annotation.ColumnAttrib;
import Entity.BaseEnity;

/**
 * 数据库操作基类
 * Created by 王彦鹏 on 2017-09-02.
 */
public class DataAccess extends SQLiteOpenHelper {

    /**
     * 数据库版本号
     */
    private static final int DATABASE_VERSION=4;

    //数据库名称
    private static final String DATABASE_NAME="data.db";

    /**
     * 创建或打开数据库
     * @param context
     */
    public DataAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.getReadableDatabase();
        //SQLiteDatabase db = null;
        //db = getReadalbeDatabase();

    }

    /**
     * TODO 创建数据库后，对数据库的操作
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*String sql1 = "drop table SystemOption";
        this.getReadableDatabase().execSQL(sql1);*/
        String sql="SELECT COUNT(*) FROM sqlite_master where type='table' and name='SystemOption'";
        Cursor cursor =sqLiteDatabase.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count<=0){
                sql="CREATE TABLE SystemOption (setname varchar PRIMARY KEY ,ivalue integer,svalue varchar,fvalue real,avalue varbinary)";
                sqLiteDatabase.execSQL(sql);
            }
        }
    }

    /**
     * TODO 更改数据库版本的操作
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    @Override

    public void onOpen(SQLiteDatabase db) {

        super.onOpen(db);
        // TODO 每次成功打开数据库后首先被执行

    }

    public List<JSONObject> Select(String sql)  {
        try {
            Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
            List<JSONObject> jsonList = new ArrayList<>();
            while (cursor.moveToNext()) {
                JSONObject jsonoRow = new JSONObject();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    int t=cursor.getType(i);
                    switch (t)
                    {
                        case 4: {
                            byte[] array=cursor.getBlob(i);
                            jsonoRow.put(cursor.getColumnName(i), array);
                            break;
                        }
                        default:
                        {
                            if (cursor.getString(i) == null){
                                jsonoRow.put(cursor.getColumnName(i), "null");
                            }else{
                                jsonoRow.put(cursor.getColumnName(i), cursor.getString(i));
                            }
                            break;
                        }
                    }
                }
                jsonList.add(jsonoRow);
            }
            return jsonList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean ExecSql(String sql)
    {
        try
        {
            this.getWritableDatabase().execSQL(sql);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean Insert(BaseEnity entity) throws Exception {

        Field[] field = entity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组

        String sql = " INSERT INTO `"+entity.getClass().getSimpleName().toLowerCase()+"` (";
        String sql1 = ") VALUES (";
        for (int j = 0; j < field.length; j++) { // 遍历所有属性
            String name = field[j].getName(); // 获取属性的名字
            if (name.equals("$change") || name.equals("serialVersionUID")){
                continue;
            }
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            Method m = entity.getClass().getMethod("get" + name);
            /*if(j == field.length-1){
                sql += "`"+name+"`";
                if (m.invoke(entity) == null || m.invoke(entity).equals("")) {
                    sql1 +="NULL)";
                }else{
                    sql1 += "'"+m.invoke(entity)+"')";
                }
            }else{*/
                sql += "`"+name+"`,";
                if (m.invoke(entity) == null || m.invoke(entity).equals("")) {
                    sql1 +="NULL,";
                }else{
                    sql1 +="'"+ m.invoke(entity)+"',";
                }
            /*}*/
        }
        return ExecSql(sql.substring(0,sql.length()-1)+sql1.substring(0,sql1.length()-1)+")");
    }


    public boolean update(BaseEnity entity) throws Exception {
        Field[] field = entity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        String sql = " update `"+entity.getClass().getSimpleName().toLowerCase()+"` set ";
        for (int j = 0; j < field.length; j++) { // 遍历所有属性
            String name = field[j].getName(); // 获取属性的名字
            if (name.equals("$change") || name.equals("serialVersionUID")){
                continue;
            }
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            Method m = entity.getClass().getMethod("get" + name);

            if (m.invoke(entity) == null || m.invoke(entity).equals("")) {
                sql += "`"+name+"` = NULL,";
            }else{
                sql += "`"+name+"` = '"+ m.invoke(entity)+"',";
            }
        }
        return ExecSql(sql.substring(0,sql.length()-1)+getWhereStr(entity,field));
    }

    public boolean delete(BaseEnity entity) throws Exception {
        // TODO Auto-generated method stub
        Field[] field = entity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        String sql = " DELETE FROM "+entity.getClass().getSimpleName().toLowerCase();
        String sql1 = "";
        for (int j = 0; j < field.length; j++) { // 遍历所有属性
            String name = field[j].getName(); // 获取属性的名字
            if (name.equals("$change") || name.equals("serialVersionUID")){
                continue;
            }
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            Method m = entity.getClass().getMethod("get" + name);
            ColAttrib attrib= field[j].getAnnotation(ColAttrib.class);
            if (attrib!=null && isPKey(attrib.value())){
                if (sql.indexOf("=")<0)
                {
                    sql1 +="`"+name+"`='"+  m.invoke(entity)+"' ";
                }
                else
                {
                    sql1 +="and `"+name+"`='"+  m.invoke(entity)+"' ";
                }
            }
        }
        if (sql1.length() > 0){
            return  ExecSql(sql+" where "+sql1);
        }
        return  ExecSql(sql);
    }


    private String getWhereStr(BaseEnity entity, Field[] fields) throws Exception
    {
        List<String> keylist= GetKeyList(fields);
        String Wstr = "";
        for (String key : keylist) {
            if (Wstr.indexOf("=")<0)
            {
                Wstr +="`"+key+"`='"+ entity.getPropertyValue(key)+"' ";
            }
            else
            {
                Wstr +="and `"+key+"`='"+ entity.getPropertyValue(key)+"' ";
            }
        }
        if (Wstr.length() == 0){
            return Wstr;
        }else{
            return " where "+Wstr;
        }

    }

    private List<String> GetKeyList(Field[] fields) {
        List<String> list=new ArrayList<String>();
        for (Field field : fields) {
            ColAttrib attrib= field.getAnnotation(ColAttrib.class);
            if (attrib==null)
            {
                continue;
            }
            for (ColumnAttrib colttrib : attrib.value()) {
                if (colttrib== ColumnAttrib.PRIMARYKEY)
                {
                    list.add(field.getName());
                }
            }
        }
        return list;
    }

    private boolean isPKey(ColumnAttrib[] colattriblist) {

        for (ColumnAttrib columnAttrib : colattriblist) {
            if (columnAttrib== ColumnAttrib.PRIMARYKEY)
            {
                return true;
            }
        }
        return false;
    }
}

