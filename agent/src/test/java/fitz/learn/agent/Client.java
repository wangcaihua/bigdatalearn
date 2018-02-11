package fitz.learn.agent;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static Moveable m;
    private static List<String> list;

    @BeforeClass
    public static void init() {
        m = new Car();
        list = new ArrayList<String>();
    }

    @Before
    public void addElements() {
        list.add("China");
        list.add("USA");
        list.add("JP");
    }

    @After
    public void clear() {
        list.clear();
    }

    @Test
    public void staticAgent() {
        TankTimeProxy ttp = new TankTimeProxy(m);
        TankLogProxy tlp = new TankLogProxy(ttp);
        tlp.start();
        tlp.move(list);
        tlp.stop();
    }

    @Test
    public void dynamicAgent() {
        MoveableHandler h = new MoveableHandler(m);
        Moveable proxy = (Moveable) Proxy.newProxyInstance(
                Client.class.getClassLoader(),
                new Class[]{Moveable.class}, h);

        proxy.start();
        proxy.move(list);
        proxy.stop();
    }

    @Test
    public void selfWriteDynamicAgent() {
        String med = MyProxy.getMethod(Moveable.class);
    }

}
