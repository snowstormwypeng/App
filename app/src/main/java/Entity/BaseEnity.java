package Entity;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by 王彦鹏 on 2017-09-01.
 */

public class BaseEnity {
    public String toString() {
        Gson gson=new Gson();
        return gson.toJson(this);
    }
    public void LoadJson(String jsonStr)
    {
        try {
            JSONObject json = new JSONObject(jsonStr);
            Field[] field = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组

            for (int j = 0; j < field.length; j++) { // 遍历所有属性
                String name = field[j].getName(); // 获取属性的名字
                name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                // // 获取属性的类型
                try {
                    if (!name.equals("$change") && !name.equals("SerialVersionUID")){
                        Method m = this.getClass().getMethod("set" + name, field[j].getType());
                        String type = field[j].getType().getSimpleName().toLowerCase();// getGenericType().toString();
                        if (json.isNull(field[j].getName()))
                        {
                            continue;
                        }

                        switch (type) {
                            case "byte": {
                                m.invoke(this, (byte)json.getInt(field[j].getName()));
                                break;
                            }
                            case "integer":
                            case "int":
                            {
                                m.invoke(this, json.getInt(field[j].getName()));
                                break;
                            }
                            case "char":
                            {
                                m.invoke(this, json.getString(field[j].getName()).charAt(0));
                                break;
                            }
                            case "string":
                            {
                                m.invoke(this, json.getString(field[j].getName()));
                                break;
                            }
                            case "boolean":
                            {
                                m.invoke(this, json.getBoolean(field[j].getName()));
                                break;
                            }
                            case "double":
                            {
                                m.invoke(this, json.getDouble(field[j].getName()));
                                break;
                            }
                            case "long":
                            {
                                m.invoke(this, json.getLong(field[j].getName()));
                                break;
                            }
                            default: {
                                m.invoke(this, json.get(field[j].getName()));
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getPropertyValue(String PName) throws Exception
    {
        Field[] fields = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(PName))
            {
                String name = field.getName(); // 获取属性的名字
                name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                Method m = this.getClass().getMethod("get" + name);
                return m.invoke(this);
            }
        }
        return null;
    }
}

