package NetComm;

import org.json.JSONObject;

import Entity.BaseEnity;
import Interface.IInterface;
import Listener.IAsynListener;

/**
 * Created by 王彦鹏 on 2017/9/28.
 */

public interface INetComm extends IInterface {
     String Get(String url) throws Exception;
     String Get(String url, String contentType) throws Exception;
     void  Get(String url, IAsynListener callback, String contentType) throws Exception;
     void  Get(String url, IAsynListener callback) throws Exception;
     String Get(String url, JSONObject param) throws Exception;
     void  Get(String url, JSONObject param, IAsynListener callback) throws Exception;

     void Post(String url, String postData, IAsynListener callback) throws Exception;
     void Post(String url, String postData, IAsynListener callback, String contentType) throws Exception;
     String Post(String url, String postData) throws Exception;
     String Post(String url, String postData, String contentType) throws Exception;

     void CallWebService(String url, final String IName, final IAsynListener callback, BaseEnity... args) throws Exception;
     JSONObject CallWebService(String url, String IName, BaseEnity... args) throws Exception;

}
