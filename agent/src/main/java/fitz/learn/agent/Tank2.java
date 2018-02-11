package fitz.learn.agent;


import java.util.List;

public class Tank2 extends Tank {
    @Override
    public void move(List<String> list) {
        long start = System.currentTimeMillis();
        super.move(list);
        long stop = System.currentTimeMillis();
        System.out.println("time:" + (stop-start));
    }
}
