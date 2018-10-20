package Factory;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import Annotation.Filter;
import Filter.IFilter;


public class ClassProxy implements InvocationHandler {
	//目标对象  
    private Object targetObject;
    private Object[] CreateArgs;
    /** 
     * 创建动态代理类 
     * @return 
     * @return object(代理类) 
     */  
    public <T> T CreateProxy(Object targetObject, Object[] args){
        this.targetObject = targetObject;   
        CreateArgs=args;
        return (T) Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
        		targetObject.getClass().getInterfaces(),this);
                  
    }  
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  throws Exception {
		Object obj = null;
		try {
			Filter filter = method.getAnnotation(Filter.class);
			List<IFilter> filterobjlist = new ArrayList<IFilter>();
			if (filter != null) {
				Log.d("Filter", String.format("存在 Filter:%s", filter.value().toString()));
				for (Class<?> iFilter : filter.value()) {
					Log.d("Filter", String.format("准备查找 Filter：%s", iFilter.getName()));
					IFilter filterobj = Factory.GetInstance(iFilter, CreateArgs);
					if (filterobj != null) {
						Log.d("Filter", String.format("找到Filter实现：%s", filterobj.getClass().getName()));
					} else {
						Log.d("Filter", String.format("未找到Filter实现：%s", iFilter.getName()));
					}
					filterobjlist.add(filterobj);
					Object ret = filterobj.Before(targetObject, method, args);
					if (ret != null) {
						return ret;
					}
				}

			}
			obj = method.invoke(targetObject, args);
			for (IFilter iFilter : filterobjlist) {
				Object ret= iFilter.After(targetObject,method,args,obj );
				if (ret!=null)
				{
					return ret;
				}
			}
		}
		catch (TimeoutException e)
		{
			throw e;
		}
		catch (Exception e) {
			if (((InvocationTargetException) e).getTargetException() instanceof TimeoutException)
			{
				throw new TimeoutException();
			}
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

 
}
