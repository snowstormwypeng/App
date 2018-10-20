package Filter;

import java.lang.reflect.Method;

import Interface.IInterface;

public  interface IFilter extends IInterface {
	Object Before(Object sender, Method method, Object[] args) throws Exception;
	Object After(Object sender, Method method, Object[] args, Object retvalue) throws Exception;
}
