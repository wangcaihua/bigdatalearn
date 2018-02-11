package fitz.learn.agent;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
//noinspection Since15
import java.lang.reflect.Parameter;

public class MyProxy {
    private static String rt = "\r\n";

    // create a MyProxy object
    public static Object newProxyInstance(Class<?> iface, InvocationHandler h) {
        return null;
    }

    public static String getMethod(Class<?> iface) {
        Method[] methods = iface.getMethods();
        if (methods.length == 0) {
            return "";
        } else {
            for (Method method: methods) {
                System.out.println(method.getName());
                for (Class<?> param: method.getParameterTypes()) {
                    System.out.println(param.getName());
                }
                //noinspection Since15
                for (Parameter parm:method.getParameters()) {
                    //noinspection Since15
                    System.out.println(parm.getName());
                    System.out.println(parm.getClass());
                }
            }
        }
        return null;

    }
}
