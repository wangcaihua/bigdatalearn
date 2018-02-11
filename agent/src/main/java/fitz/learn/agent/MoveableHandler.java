package fitz.learn.agent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class MoveableHandler implements InvocationHandler {
    private Moveable m;

    public MoveableHandler(Moveable m) {
        this.m = m;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Go into method:" + method.getName());
        long start = System.currentTimeMillis();
        Object res = method.invoke(m, args);
        long stop = System.currentTimeMillis();
        System.out.println("Go out of method:" + method.getName());
        System.out.println("total time: " + (stop - start));
        return res;
    }
}
