package Factory;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by 王彦鹏 on 2017-09-06.
 */

public class Factory {
    private static ArrayList<Class> Classlist;
    public static <T> T GetInstance(Class<?> classType, Object[] args)
    {
        try
        {
            Constructor c1;
            if (args !=null)
            {
                Class[] classlist=new Class[args.length];
                for (int i=0;i<args.length;i++)
                {
                    if (args[i] instanceof Context)
                    {
                        classlist[i] =Context.class;
                    }
                    else {
                        classlist[i] = args[i].getClass();
                    }
                }

                c1=classType.getConstructor(classlist);
            }
            else
            {
                c1=classType.getConstructor();
            }
            c1.setAccessible(true);
            ClassProxy proxy = new ClassProxy();
            return  (T) proxy.CreateProxy(c1.newInstance(args),args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> GetInstanceList(Class<?> interfaceType, Object[] args)
    {
        try
        {
            List<T> list=new ArrayList<T>();
            ArrayList<Class> cList=getAllClassByInterface(interfaceType);
            for (Class c : cList) {
                Constructor c1;
                if (args !=null)
                {
                    Class[] classlist=new Class[args.length];
                    for (int i=0;i<args.length;i++)
                    {
                        classlist[i]=args[i].getClass();
                    }
                    c1=c.getDeclaredConstructor(classlist);

                }
                else
                {
                    c1=c.getDeclaredConstructor();
                }
                c1.setAccessible(true);

                ClassProxy proxy = new ClassProxy();
                T t= (T)proxy.CreateProxy(c1.newInstance(args),args);
                list.add(t);
            }
            return list;

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static ArrayList<Class> getAllClassByInterface(Class clazz){
        ArrayList<Class> list = new ArrayList<>();
        //判断是否是一个接口
        if (clazz.isInterface()) {
            try {
                String pname=clazz.getPackage().getName();
                pname=pname.substring(0,pname.lastIndexOf("."));
                ArrayList<Class> allClass = getAllClass(pname);
                if (allClass.size() == 0) {
                    Log.d("Factory","获取包下所有类为空");
                }
                /**
                 * 循环判断路径下的所有类是否实现了指定的接口
                 * 并且排除接口类自己
                 */
                for (int i = 0; i < allClass.size(); i++) {
                    /**
                     * 判断是不是同一个接口
                     * 该方法的解析，请参考博客：
                     * http://blog.csdn.net/u010156024/article/details/44875195
                     */
                    if (clazz.isAssignableFrom(allClass.get(i))) {
                        if (!clazz.equals(allClass.get(i))) {//自身并不加进去
                            Log.d("Factory","添加实现类："+allClass.get(i).getName());
                            list.add(allClass.get(i));
                        }else {

                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(String.format("反射接口实向类时出现异常，原因：%s", e.getMessage()));
                Log.d("Factory",e.getMessage());
                throw e;
            }
        }else {
            Log.d("Factory", String.format("对象[%s]不是一个Interface", clazz.getSimpleName()));
            list.add(clazz);
            //如果不是接口不作处理
        }
        return list;
    }

    /**
     * 从一个指定路径下查找所有的类
     * @packagename name
     */
    @SuppressWarnings("rawtypes")
    private static ArrayList<Class> getAllClass(String packagename) {
        if (Classlist!=null)
        {
            return Classlist;
        }
        Classlist = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packagename.replace('.', '/');
        try {
            ArrayList<File> fileList = new ArrayList<>();
            /**
             * 这里面的路径使用的是相对路径
             * 如果大家在测试的时候获取不到，请理清目前工程所在的路径
             * 使用相对路径更加稳定！
             * 另外，路径中切不可包含空格、特殊字符等！
             * 本人在测试过程中由于空格，吃了大亏！！！
             */
            Enumeration<URL> enumeration = classLoader.getResources(path);
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                fileList.add(new File(url.getFile()));
            }
            for (int i = 0; i < fileList.size(); i++) {
                Classlist.addAll(findClass(fileList.get(i),packagename));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Classlist;
    }

    /**
     * 如果file是文件夹，则递归调用findClass方法，或者文件夹下的类
     * 如果file本身是类文件，则加入list中进行保存，并返回
     * @param file
     * @param packagename
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static ArrayList<Class> findClass(File file, String packagename) {
        ArrayList<Class> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                assert !file2.getName().contains(".");//添加断言用于判断
                ArrayList<Class> arrayList = findClass(file2, packagename+"."+file2.getName());
                list.addAll(arrayList);
            }else if(file2.getName().endsWith(".class")){
                try {
                    //保存的类文件不需要后缀.class
                    list.add(Class.forName(packagename + '.' + file2.getName().substring(0,
                            file2.getName().length()-6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
