package fitz.learn.agent;


import java.util.List;
import java.util.Random;

public class Car implements Moveable {
    public void move(List<String> list) {
        System.out.println("Car Moving ...");
        System.out.println(list);
        try {
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Car is starting now ...");
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        System.out.println("Car is stoped ...");
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
