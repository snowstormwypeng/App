package Struct;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import Annotation.StructIndex;

public class StructBase {

	private StructBase structBase;

	public Field GetFild(int index)
	{
		Field[] field = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		for (int j = 0; j < field.length; j++) { // 遍历所有属性
			StructIndex t = field[j].getAnnotation(StructIndex.class);
			if (t != null) {
				if (t.value() == index) {
					return field[j];
				}
			}
		}
		return null;
	}
	/**
	 * 获取实体数据的长度（字节流长度） @return 正常返回真实的长度， 异常返回 0 。 @throws Exception @throws
	 */
	public int GetLen() throws Exception {
		Field[] field = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		int len = 0;
		for (int j = 0; j < field.length; j++) { // 遍历所有属性
			Field f=GetFild(j);
			if (f==null)
			{
				continue;
			}
			if (f.getType().getName().indexOf("BinaryType") < 0)
			{
				continue;
			}
			String name = f.getName(); // 获取属性的名字
			name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
			String type = f.getType().getName();// getGenericType().toString();
														// // 获取属性的类型

			Method m = this.getClass().getMethod("get" + name);
			Object instance = m.invoke(this);
			int datalen = ((BinaryType) instance).getLen();
			len += datalen;


		}
		return len;

	}

	/**
	 * 加载字节流数据到对象
	 * 
	 *            字节流
	 * @throws Exception
	 */
	public byte LoadData(byte[] arraydata) {
		structBase = this;
		Field[] field = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		int len = 0;
		for (int j = 0; j < field.length; j++) { // 遍历所有属性
			Field f=GetFild(j);
			if (f==null)
			{
				continue;
			}
			if (f.getType().getName().indexOf("BinaryType") < 0)
			{
				continue;
			}
			String name = f.getName(); // 获取属性的名字
			name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
			String type = f.getType().getName();// getGenericType().toString();
														// // 获取属性的类型
			try {
				Method m = this.getClass().getMethod("get" + name);
				Object instance = m.invoke(this);
				int datalen = ((BinaryType) instance).getLen();
				byte[] data = new byte[datalen];
				System.arraycopy(arraydata, len, data, 0, data.length);
				((BinaryType) instance).setData(data);
				len += datalen;
			}
			catch (Exception e) {

				return -1;
			}
		}
		return 0;
	}

	/**
	 * 把实体对象转为字节流
	 * 
	 * @return 成功返回字节流，失败返回 null;
	 * @throws Exception
	 */

	public byte[] ToArray() throws Exception {
		byte[] arraydata = new byte[GetLen()];
		Field[] field = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		int len = 0;
		for (int j = 0; j < field.length; j++) { // 遍历所有属性
			Field f=GetFild(j);
			if (f==null)
			{
				continue;
			}
			if (f.getType().getName().indexOf("BinaryType") < 0)
			{
				continue;
			}
			String name = f.getName(); // 获取属性的名字
			name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
			String type = f.getType().getName();// getGenericType().toString();
														// // 获取属性的类型

			Method m = this.getClass().getMethod("get" + name);
			Object instance = m.invoke(this);
			int datalen = ((BinaryType) instance).getLen();
			byte[] data = ((BinaryType) instance).getData();
			System.arraycopy(data, 0, arraydata, len, datalen);
			len += datalen;
		}
		return arraydata;
	}

	@Override
	public String toString() {
		String json="{";
		Field[] field = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		int len = 0;
		for (int j = 0; j < field.length; j++) { // 遍历所有属性
			try {
				if (field[0] == null) {
					continue;
				}
				if (field[j].getType().getName().indexOf("BinaryType") >= 0) {
					String name = field[j].getName(); // 获取属性的名字
					json+="\""+name+"\":";
					name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
					String type = field[j].getType().getName();// getGenericType().toString();
					// // 获取属性的类型

					Method m = this.getClass().getMethod("get" + name);
					Object instance = m.invoke(this);
					json+=instance.toString()+",";
				}
			}
			catch (Exception e)
			{

			}
		}
		json=json.substring(0,json.length()-1);
		json+="}";
		return json;
	}

	public void loadJson(String jsonStr) {
		try
		{
			JSONObject jdata = new JSONObject(jsonStr);
			Field[] field = this.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
			int len = 0;
			for (int j = 0; j < field.length; j++) { // 遍历所有属性
				try {
					if (field[0] == null) {
						continue;
					}
					if (field[j].getType().getName().indexOf("BinaryType") >= 0) {
						String name = field[j].getName(); // 获取属性的名字
						name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
						Method m = this.getClass().getMethod("get" + name);
						Object instance = m.invoke(this);
						((BinaryType)instance).setValue(jdata.get(field[j].getName()));
					}
				}
				catch (Exception e)
				{
                    e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{

		}
	}
}
