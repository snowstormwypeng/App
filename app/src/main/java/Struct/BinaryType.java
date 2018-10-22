package Struct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Helper.EnjoyTools;

/**
 * Created by 王彦鹏 on 2018-01-31.
 */
public class BinaryType {
	/**
	 * 长度
	 */
	private int len=0;
	/**
	 * 数据内容
	 */
	private byte[] data;
	private Class<?> type;
	
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		if (data.length<=this.data.length) {
			System.arraycopy(data, 0, this.data, 0, data.length);
		}
		else
		{
			System.arraycopy(data, 0, this.data, 0, this.data.length);
		}
	}

	public void setDataAndLen(byte[] data) {
		this.data = data;
		this.len=data.length;
	}

	/**
	 * 构造函数
	 * @param len 长度
	 * @param type 数据类型
	 */
	public BinaryType(int len, Class<?> type)
	{
		this.len=len;
		this.data=new byte[len];
		this.type=type;
	}
	/**
	 * 获取对象的整形值
	 * @return
	 */
	public long getInt()
	{
		long v = 0;
		for (int i = this.len - 1; i >= 0; i--) {
			v = (v+  ((long)(this.data[i] & 0xff) << (i * 8)));
		}
		return v & 0xffffffff;
	}
	/**
	 * 获取对象的短整形值（两字节）
	 * @return
	 */
	public short getShot()
	{
		short v = 0;
        for (int i = this.len - 1; i >= 0; i--) {
            v = (short)(v+ (this.data[i] << (i * 8)));
        }
        return v;
	}
	/**
	 * 获取Byte值
	 * @return
	 */
	public byte getByte()
	{	
        return data[0];
	}
	
	/**
	 * 获取字符串值
	 * @return
	 */
	public String getString()
	{	
		try
		{
			List<Byte> list=new ArrayList<>();
			for(int i=0;i<this.data.length;i++)
			{
				if (this.data[i]!=0)
				{
					list.add(this.data[i]);
				}
				else
				{
					break;
				}
			}			
			byte[] a=new byte[list.size()];
			for(int i=0;i<list.size();i++)
			{
				a[i]=list.get(i);
			}
			String str= new String(a,"gb2312");
			return str;
		}
		catch (Exception e) {
			return "";
		}
	}
	/**
	 * 设置整数数据
	 * @param value
	 */
	public void setInt(long value)
	{
		for(int i=0;i<data.length;i++)
		{
			data[i] = (byte) (value >>(i*8) & 0xFF);
		}
		//data[0] = (byte) (value & 0xFF);
        //data[1] = (byte) ((value >> 8) & 0xFF);
        //data[2] = (byte) ((value >> 16) & 0xFF);
        //data[3] = (byte) ((value >> 24) & 0xFF);
	}
	/**
	 * 设置整数数据
	 * @param value
	 */
	public void setInt3(int value)
	{
		data[0] = (byte) (value & 0xFF);
		data[1] = (byte) ((value >> 8) & 0xFF);
		data[2] = (byte) ((value >> 16) & 0xFF);
	}
	/**
	 * 设置字节数据
	 * @param value
	 */
	public void setByte(byte value)
	{
		data[0]=value;
	}
	/**
	 * 设置字符串值
	 * @param value
	 */
	public void setString(String value)
	{
		if (value==null)
		{
			value="";
		}
		byte[] buff= value.getBytes();
		System.arraycopy(buff, 0, data, 0, Math.min(buff.length, data.length));
	}
	/**
	 * 设置字符串值
	 * @param value
	 */
	public void setHexString(String value)
	{

		byte[] buff= EnjoyTools.HexStrToBytes(value);
		System.arraycopy(buff, 0, data, 0, data.length);
	}

	/**
	 * 设置时间值，
	 * @param value 格式：yyyy-MM-dd HH:mm:ss
	 */
	public void setDateTime(String value)
	{
		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar date = Calendar.getInstance();
			date.setTime(dateFormat.parse(value));

		//Date date=new Date(value);
//        date.setYear(Integer.parseInt(value.substring(0,4)));
//        date.setMonth(Integer.parseInt(value.substring(5,7)));
//        date.setDate(Integer.parseInt(value.substring(8,10)));
//        date.setHours(Integer.parseInt(value.substring(11,13)));
//        date.setMinutes(Integer.parseInt(value.substring(14,16)));
//        date.setSeconds(Integer.parseInt(value.substring(17,19)));


			switch (data.length) {
				case 20: {
					setString(value);
					break;
				}
				case 4: {
					long l = 0;
					int tmp = date.get(Calendar.YEAR);
					if (tmp < 2010) {
						tmp = 2010;
					}
					l = (tmp - 2010) << 28;
					tmp =(date.get(Calendar.MONTH)+1) << 24;
					l = l + tmp;
					tmp =date.get(Calendar.DAY_OF_MONTH) << 19;
					l = l + tmp;
					tmp = date.get(Calendar.HOUR_OF_DAY) << 14;
					l = l + tmp;
					tmp = date.get(Calendar.MINUTE) << 7;
					l = l + tmp;
					tmp = date.get(Calendar.SECOND);
					l = l + tmp;
					setInt(l);
					break;
				}
				case 6: {
					data[0] = (byte) Math.max(00, date.get(Calendar.YEAR) - 2000);
					data[1] = (byte) (date.get(Calendar.MONTH)+1);
					data[2] = (byte) date.get(Calendar.DAY_OF_MONTH);
					data[3] = (byte) date.get(Calendar.HOUR_OF_DAY);
					data[4] = (byte) date.get(Calendar.MINUTE);
					data[5] = (byte) date.get(Calendar.SECOND);

					break;
				}
				case 3: {
					data[0] = (byte) Math.max(00, date.get(Calendar.YEAR) - 2000);
					data[1] = (byte) (date.get(Calendar.MONTH)+1);
					data[2] = (byte) date.get(Calendar.DAY_OF_MONTH);

					break;
				}
				default: {

				}
			}
		}
		catch (Exception e) {
		}
	}


	/**
	 * 设置双字节值
	 * @param value
	 */
	public void setShort(short value)
	{
		 data[0] = (byte) (value & 0xFF);
		 data[1] = (byte) ((value >> 8) & 0xFF);
	        
	}
	
	/**
	 * 设置数字数据
	 * @param value 字节数组
	 * @param strPos 从第几个开始（第一个为：0）
	 * @param len 需要拷贝几个字节
	 */
	public void setBuff(byte[] value,int strPos,int len)
	{
		try
		{
			System.arraycopy(value, strPos, data, 0, len);
		}
		catch(Exception E)
		{
			System.out.print(E.getMessage());
		}
	}
	public String toString()
	{
		switch (type.getName())
		{
			case "byte":
			{
				return String.valueOf(data[0]);
			}
			case "java.lang.String":
			{
				return "\"" +getString()+"\"";
			}
			case "int":
			case "short":
			{
				long l=getInt();
				return String.valueOf(l);
			}
			case "java.util.Date":
			{
				switch (data.length)
				{
					case 4:
					{
						long i =getInt();
						int second = (int) (i & 0x7f);
						int minute = (int) (((i >> 7) & 0xFF) & 0x7f);
						int hour = (int) (((i >> 14) & 0xFF) & 0x1f);
						int day = (int) (((i >> 19) & 0xFF) & 0x1f);
						int month = (int) (((i >> 24) & 0xFF) & 0x0f);
						int year = (int) (((i >> 28) & 0xFF) & 0x0f);
						year = year + 2010;
						return String.format("\"%04d-%02d-%02d %02d:%02d:%02d\"",year,month,day,hour,minute,second);

					}
					case 6:
					{
						return String.format("\"%04d-%02d-%02d %02d:%02d:%02d\"",data[0]+2000,data[1],data[2],data[3],data[4],data[5]);
					}
					default:
					{
						return "\""+ getString()+"\"";
					}
				}
			}
			default:
			{
				return data.toString();
			}
		}
	}
	public String getDateTime() {
		switch (data.length) {
			case 4: {
				long i = getInt();
				int second = (int) (i & 0x7f);
				int minute = (int) (((i >> 7) & 0xFF) & 0x7f);
				int hour = (int) (((i >> 14) & 0xFF) & 0x1f);
				int day = (int) (((i >> 19) & 0xFF) & 0x1f);
				int month = (int) (((i >> 24) & 0xFF) & 0x0f);
				int year = (int) (((i >> 28) & 0xFF) & 0x0f);
				year = year + 2010;
				return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);

			}
			case 6: {
				return String.format("%04d-%02d-%02d %02d:%02d:%02d", data[0] + 2000, data[1], data[2], data[3], data[4], data[5]);
			}
			default: {
				return getString() ;
			}
		}
	}


	public void setValue(Object value)
	{
		switch (type.getName())
		{
			case "byte":
			{
                int v=(Integer)value;
				setByte((byte)v);
				break;
			}
			case "java.lang.String":
			{
				setString(value.toString());
				break;
			}
			case "int":
			{
				setInt((Integer)value);
				break;
			}
			case "short":
			{
                int v=(Integer)value;
				setShort((short)v);
				break;
			}
			case "java.util.Date":
			{
				setDateTime(value.toString());
				break;
			}
			default:
			{
				setHexString(value.toString());
				break;
			}
		}
	}
}
