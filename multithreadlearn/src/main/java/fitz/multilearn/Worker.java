package fitz.multilearn;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Worker implements Runnable {
    private String inFilePrefix;
    private int dim;
    private Map<String, String> conf;

    public Worker(String inFilePrefix, int dim, Map<String, String> conf) {
        this.inFilePrefix = inFilePrefix;
        this.dim = dim;
        this.conf = conf;
    }


    private double[] parse(String line) throws Exception {
        String splitor = conf.get("splitor");
        if (splitor == null || splitor.length() == 0) {
            throw new Exception("No splitor found!");
        }

        String[] vectorS = line.trim().split(splitor);

        double[] vectorD = new double[dim];
        int idx = 0;
        for (String ele: vectorS) {
            vectorD[idx] = Double.parseDouble(ele);
            idx++;
        }

        return vectorD;
    }

    private List<double[]> preProcess() {
        BufferedReader bw = null;
        try {
            String inFileName = inFilePrefix + Thread.currentThread().getId();
            bw = new BufferedReader(new FileReader(inFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<double[]> data = new ArrayList<double[]>();


        if (bw != null) {
            try {
                String line = bw.readLine();
                data.add(parse(line));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    public void run() {
        List<double[]> data = preProcess();

    }
}
