package Helper;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Annotation.Description;

/**
 * Created by 王彦鹏 on 2016/9/14.
 */
public class EnjoyTools {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    public static byte[] encrypt(byte[] InData, byte[] Key) throws Exception {
        return AESKeyModel.encrypt(InData, Key);
    }

    public static long GetTimestamp() {
        return new Date().getTime();
    }

    /**
     * 字节流转16进制字符串
     * @param inarray 字节流
     * @return 字符串
     */
    public static String ByteArrayToHexString(byte[] inarray) { // converts byte
        // arrays to string
        int i, j, in;
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F" };
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    public static byte[] decrypt(byte[] InData, byte[] Key) throws Exception {
        if (InData.length % 16 != 0 || InData.length == 0) {
            return null;
        }
        byte[] ResData = new byte[InData.length];
        for (int i = 1; i <= InData.length / 16; i++) {
            byte[] in = new byte[16];
            System.arraycopy(InData, (i - 1) * 16, in, 0, 16);
            byte[] out= AESKeyModel.decrypt(in, Key);
            System.arraycopy(out, 0,ResData,(i - 1) * 16,16);
        }

        return ResData;
    }
    private  static byte toByte(char c)
    {
        byte b=(byte)"0123456789ABCDEF".indexOf(c);
        return b;
    }
    public static byte[] HexStrToBytes(String HexStr)
    {
        HexStr=HexStr.toUpperCase();
        int len=(HexStr.length()/2);
        byte[] result=new byte[len];
        char[] achar=HexStr.toCharArray();
        for(int i=0;i<len;i++)
        {
            int pos=i*2;
            result[i]=(byte)(toByte(achar[pos])<<4 | toByte(achar[pos+1]));
        }
        return result;
    }



    public static String getEnumDescription(Enum state) {
        try {
            Description description = state.getClass().getField(state.toString()).getAnnotation(Description.class);
            return description.value();
        } catch (Exception e) {
            return "未知类型";
        }
    }
    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param strDate
     * @return
     */
    public static boolean isDate(String strDate) {
        return true;
    }

    public static boolean IDCardValidate(String IDStr) throws Exception {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            throw new Exception("身份证号码长度应该为15位或18位。");

        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            throw new Exception("身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。");
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效。";
            throw new Exception(errorInfo);
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围。";
                throw new Exception(errorInfo);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            throw new Exception(errorInfo);
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            throw new Exception(errorInfo);
        }

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                throw new Exception(errorInfo);
            }
        } else {
            return true;
        }
        // =====================(end)=====================
        return true;
    }

}