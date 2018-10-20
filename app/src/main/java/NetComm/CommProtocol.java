package NetComm;

import org.json.JSONObject;

import Entity.BaseEnity;
import Factory.Factory;
import Listener.IAsynListener;

/**
 * Created by 王彦鹏 on 2017-09-02.
 */

public class CommProtocol implements ICommProtocol {
    private INetComm http= Factory.GetInstance(Http.class,null);
    //在这里更换通讯协议
    @Override
    public String Get(String url) throws Exception {

        return http.Get(url);
    }

    @Override
    public String Get(String url, String contentType) throws Exception {
        return http.Get(url,contentType);
    }

    @Override
    public void Get(String url, IAsynListener callback, String contentType) throws Exception {
        http.Get(url,callback,contentType);
    }

    @Override
    public void Get(String url, IAsynListener callback) throws Exception {
        http.Get(url,callback);
    }

    @Override
    public String Get(String url, JSONObject param) throws Exception {
        return http.Get(url,param);
    }

    @Override
    public void Get(String url, JSONObject param, IAsynListener callback) throws Exception {
        http.Get(url,param,callback);
    }

    public String Post(String url, String postData) throws Exception {

        return http.Post(url,postData);
    }

    @Override
    public String Post(String url, String postData, String contentType) throws Exception {
        return http.Post(url,postData,contentType);
    }

    @Override
    public void CallWebService(String url, String IName, IAsynListener callback, BaseEnity... args) throws Exception {
        http.CallWebService(url, IName,callback,args);
    }

    @Override
    public JSONObject CallWebService(String url, String IName, BaseEnity... args) throws Exception {
        return http.CallWebService(url,IName,args);
    }

    @Override
    public  void Post(String url, String postData, IAsynListener callback) throws Exception
    {
        http.Post(url,postData,callback);
    }

    @Override
    public void Post(String url, String postData, IAsynListener callback, String contentType) throws Exception {
        http.Post(url,postData,callback,contentType);
    }




}
