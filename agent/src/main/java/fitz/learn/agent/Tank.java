package fitz.learn.agent;


import java.util.List;
import java.util.Random;

public class Tank implements Moveable {

    public void move(List<String> list) {
        System.out.println("Tank Moving ...");
        System.out.println(list);
        try {
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Tank is starting now ...");
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        System.out.println("Tank is stoped ...");
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
