package NetComm;

import android.accounts.NetworkErrorException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import Annotation.PropertyName;

import Entity.BaseEnity;
import Helper.Log;
import Listener.IAsynListener;

public class Http implements INetComm {

    /**
     * 线程执行器
     */
    private ExecutorService SingleThread;
    /**
     * 用Get方式请求服务器
     *
     * @param url         url 地址
     * @param contentType 请求数据类型
     * @return 服务器返回的数据
     */
    private String httpMethod(String url, String contentType, String method, String postData) throws Exception {
        HttpURLConnection conn = null;
        try {
            long t1,t2=0;
            t1= System.currentTimeMillis();
            URL murl = new URL(url);
            //1.得到HttpURLConnection实例化对象
            conn = (HttpURLConnection) murl.openConnection();
            //2.设置请求信息（请求方式... ...）
            //设置请求方式和响应时间rCase());
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Content-Length", String.valueOf(postData.getBytes().length));
            conn.setRequestProperty("encoding", "UTF-8"); //可以指定编码
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //不使用缓存
            conn.setUseCaches(false);
            // 设置可取
            conn.setDoInput(true);
            if (method.toUpperCase() == "POST") {
                // 设置可读
                conn.setDoOutput(true);
                //4.向服务器写入数据
                conn.getOutputStream().write(postData.getBytes());
            }
            //3.读取响应
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                // 创建高效流对象
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                // 创建StringBuilder对象存储数据
                StringBuilder response = new StringBuilder();
                String line;// 一次读取一行
                while ((line = reader.readLine()) != null) {
                    response.append(line);// 得到的数据存入StringBuilder
                }
                t2= System.currentTimeMillis();
                Log.write("Http", String.format("%s 请求(%s)\r\nUrl：%s\r\n请求数据：%s\r\n返回数据：%s\r\n请求用时：%3f",
                        method,contentType,url,postData, response.toString(),(t2-t1)/ Double.valueOf(1000)));
                return response.toString();
            } else {
                Log.write("Http", String.format("%s 请求(%s)：%s\r\n请求失败。",method,contentType,url));
                throw new Exception("请求失败");
            }
        } catch (TimeoutException e)
        {
            Log.write("Http", String.format("%s 请求(%s)：%s\r\n连接超时。",method,contentType,url));
            throw new Exception("请求超时或网络已断开。");
        }catch (SocketTimeoutException e)
        {
            throw new Exception("请求超时或网络已断开。");
        }
        catch (NetworkErrorException e)
        {
            throw new Exception("网络断开");
        }
        catch (Exception e) {
            Log.write("Http", String.format("%s 请求(%s)：%s\r\n请求异常:%s",method,contentType,url,e.getMessage()));
            e.printStackTrace();
            if(e.getMessage().indexOf("Network is unreachable")>=0)
            {
                throw new Exception("网络断开");
            }
            else {
                throw e;
            }
        } finally {
            //4.释放资源
            if (conn != null) {
                //关闭连接 即设置 http.keepAlive = false;
                conn.disconnect();
            }
        }
    }

    /**
     * 用Get方式请求服务器
     *
     * @param url         url 地址
     * @param callback    异步回调地址，当不为null时为异步调用
     * @param contentType 请求数据类型
     * @return 服务器返回的数据
     */
    public  void Get(final String url,
                     final IAsynListener callback, final String contentType) throws Exception {
        SingleThread = Executors.newSingleThreadExecutor();
        SingleThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String httpResponse = httpMethod(url, contentType, "GET", "");
                    // 回调onFinish()方法
                    callback.onFinish(Http.this, httpResponse);

                } catch (Exception e) {
                    // 回调onError()方法
                    callback.onError(this, e);
                }
            }
        });
    }
    @Override
    public void Get(String url, IAsynListener callback) throws Exception {
        Get(url,callback,"application/x-www-form-urlencoded");
    }

    @Override
    public String Get(String url, JSONObject param) throws Exception {
        Iterator iterator = param.keys();
        String str = "";
        while(iterator.hasNext()){
            String key = (String) iterator.next();
            String value = null;
            value = param.getString(key);
            str+=("&"+key+"="+ URLEncoder.encode(value,"utf-8"));
        }
        url = url+"?"+str.substring(1);
        return Get(url,"application/x-www-form-urlencoded");
    }

    @Override
    public void Get(String url, JSONObject param, IAsynListener callback) throws Exception {
        Iterator iterator = param.keys();
        String str = "";
        while(iterator.hasNext()){
            String key = (String) iterator.next();
            String value = null;
            value = param.getString(key);
            str+=("&"+key+"="+ URLEncoder.encode(value,"utf-8"));
        }
        url = url+"?"+str.substring(1);
        Get(url,callback,"application/x-www-form-urlencoded");
    }

    /**
     * 用Get方式请求服务器
     *
     * @param url 网络地址
     * @return
     */
    @Override
    public String Get(final String url) throws Exception {
        return Get(url,"application/x-www-form-urlencoded");
    }

    @Override
    public String Get(final String url, final String contentType) throws Exception {
        /*if (!PublicDefine.NetworkConnState)
        {
            throw new Exception("网络连接已断开，请先检查网络。");
        }*/
        SingleThread= Executors.newSingleThreadExecutor();
        Future<String> future= SingleThread.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return httpMethod(url,contentType,"GET","");
            }
        });
        return future.get(5, TimeUnit.SECONDS);
    }


    /**
     * 用 Post 方式请求服务器
     *
     * @param url         网络地址
     * @param postData    提交数据
     * @param callback    回调地址
     * @param contentType 请求数据类型
     * @return 服务器返回数据
     */
    public  void Post(final String url,
                              final String postData,
                              final IAsynListener callback,
                              final String contentType) throws Exception {
        SingleThread = Executors.newSingleThreadExecutor();
        SingleThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String httpResponse = httpMethod(url, contentType, "POST", postData);
                    // 回调onFinish()方法
                    callback.onFinish(Http.this, httpResponse);

                } catch (Exception e) {
                    // 回调onError()方法
                    callback.onError(this, e);
                }
            }
        });
    }

    @Override
    public String Post(final String url, final String postData) throws Exception {
        return Post(url,postData,"application/x-www-form-urlencoded");
    }

    @Override
    public String Post(final String url, final String postData, final String contentType) throws Exception {
//        if (!PublicDefine.NetworkConnState)
//        {
//            throw new Exception("网络连接已断开，请先检查网络。");
//        }
        SingleThread= Executors.newSingleThreadExecutor();
        Future<String> future= SingleThread.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return httpMethod(url,contentType,"POST",postData);
            }
        });
        return future.get(5, TimeUnit.SECONDS);
    }


    @Override
    public void Post(String url, String postData, IAsynListener callback) throws Exception {
        Post(url, postData,callback,"application/x-www-form-urlencoded");

    }


    private JSONObject LoadNodeChild(Node node) throws JSONException {
        JSONObject json = new JSONObject();
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node subnode=node.getChildNodes().item(i);
            String key =subnode.getNodeName();
            Object value = null;
            if (subnode.hasChildNodes()&& (!subnode.getChildNodes().item(0).getNodeName().equals("#text"))) {
                value = LoadNodeChild(subnode);
            } else {
                value = subnode.getTextContent();
            }
            if (json.has(key)){
                Object oldJson = json.get(key);
                if (oldJson instanceof JSONObject){
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(oldJson);
                    jsonArray.put(value);
                    json.remove(key);
                    json.put(key, jsonArray);
                }else{
                    ((JSONArray)oldJson).put(value);
                    json.put(key,oldJson);
                }
            }else{
                json.put(key, value);
            }
        }
        return json;
    }

    private String GetPropertyName(Field f)
    {
        PropertyName pname = f.getAnnotation(PropertyName.class);
        if (pname==null)
        {
            return f.getName();
        }
        else
        {
            return pname.value();
        }
    }

    private String GetKeyValue(BaseEnity enity) throws Exception {
        String values = "";
        Field[] field = enity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组

        for (int j = 0; j < field.length; j++) { // 遍历所有属性
            String name = field[j].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法

            try {
                Method m = enity.getClass().getMethod("get" + name);
                Object instance = m.invoke(enity);
                if (instance.getClass().getSimpleName().equals("ArrayList")){
                    for (int i = 0; i < ((List)instance).size(); i++) {
                        if (((List) instance).get(i).getClass().getSuperclass().getSimpleName().equals("BaseEnity")) {
                            values += "<" + GetPropertyName(field[j]) + ">" + GetKeyValue((BaseEnity) ((List) instance).get(i)) + "</" + GetPropertyName(field[j]) + ">\n";
                        } else {
                            values += "<" + GetPropertyName(field[j]) + ">" + ((List) instance).get(i).toString() + "</" + GetPropertyName(field[j]) + ">\n";
                        }
                    }
                }else if (instance.getClass().getSuperclass().getSimpleName().equals("BaseEnity")) {
                    values += "<" + GetPropertyName(field[j]) + ">" + GetKeyValue((BaseEnity) instance) + "</" + GetPropertyName(field[j]) + ">\n";
                } else {
                    values += "<" + GetPropertyName(field[j]) + ">" + instance.toString() + "</" + GetPropertyName(field[j]) + ">\n";
                }
            }
            catch (Exception e)
            {

            }
        }
        return values;
    }

    private String GetCallWebServiceRequestData(String url, final String IName, BaseEnity... args) throws Exception
    {
        String pdata = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                /*"<soap:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\""+*/
                "  <soap:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" >\n" +
                "<%s xmlns=\"http://tempuri.org/\">\n";
        if (args!=null) {
            for (BaseEnity entity:args ) {
                pdata+=GetKeyValue(entity);
            }

        }
        pdata += "</%s>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
        pdata = String.format(pdata, IName, IName);
        return pdata;
    }

    @Override
    public void CallWebService(String url, final String IName, final IAsynListener callback, BaseEnity... args) throws Exception {

        String pdata=GetCallWebServiceRequestData(url,IName,args);

        Post(url, pdata, new IAsynListener() {
            @Override
            public void onFinish(Object sender, Object data) {
                try {
                    String resxml = data.toString();
                    DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = xml.newDocumentBuilder();
                    Document dom = builder.parse(new InputSource(new ByteArrayInputStream(resxml.getBytes("utf-8"))));

                    Element root = dom.getDocumentElement();
                    NodeList items = root.getElementsByTagName(IName + "Response");//查找所有person节点
                    Node response = items.item(0);
                    JSONObject resJson = LoadNodeChild(response);
                    callback.onFinish(Http.this, resJson);
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onError(Object sender, Exception e) {
                callback.onError(this,e);
            }
        }, "text/xml; charset=utf-8");

    }

    @Override
    public JSONObject CallWebService(String url, String IName, BaseEnity... args) throws Exception {

        String pdata=GetCallWebServiceRequestData(url,IName,args);

        String resxml = Post(url, pdata,"text/xml; charset=utf-8");
        DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = xml.newDocumentBuilder();
        Document dom = builder.parse(new InputSource(new ByteArrayInputStream(resxml.getBytes("utf-8"))));

        Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName(IName + "Response");//查找所有person节点
        Node response = items.item(0);
        JSONObject resJson = LoadNodeChild(response);
        return resJson;
    }
}
