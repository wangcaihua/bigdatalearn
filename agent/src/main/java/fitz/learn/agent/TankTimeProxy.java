package fitz.learn.agent;


import java.util.List;

public class TankTimeProxy implements Moveable {
    private Moveable t;

    public TankTimeProxy(Moveable t) {
        this.t = t;
    }

    public void move(List<String> list) {
        long start = System.currentTimeMillis();
        t.move(list);
        long stop = System.currentTimeMillis();
        System.out.println("time:" + (stop-start));
    }

    public void start() {
        long start = System.currentTimeMillis();
        t.start();
        long stop = System.currentTimeMillis();
        System.out.println("time:" + (stop-start));
    }

    public void stop() {
        long start = System.currentTimeMillis();
        t.stop();
        long stop = System.currentTimeMillis();
        System.out.println("time:" + (stop-start));
    }
}
