package fitz.learn.agent;


import java.util.List;

public class TankLogProxy implements Moveable {
    private Moveable t;

    public TankLogProxy(Moveable t) {
        this.t = t;
    }

    public void move(List<String> list) {
        System.out.println("Hey, you are in agent!");
        t.move(list);
    }

    public void start() {
        System.out.println("Hey, you are in agent!");
        t.start();
    }

    public void stop() {
        System.out.println("Hey, you are in agent!");
        t.stop();
    }
}
