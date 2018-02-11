package fitz.multilearn.mllib.logisticregression;


import fitz.multilearn.paramerer.Paremeter;

public class LRParemeters implements Paremeter {
    private int clock = 0;
    private int dim;
    private double[] weight;
    private double bias;

    public LRParemeters(int dim) {
        this.dim = dim;
        weight = new double[dim];
        for (int i= 0; i < dim; i++) {
            weight[i] = Math.random() * 0.0001;
        }
        bias = 0.0;
    }

    public LRParemeters(int clock, int dim, double[] weight, double bias) {
        this.clock = clock;
        this.dim = dim;
        this.weight = weight;
        this.bias = bias;
    }

    public LRParemeters copy() {
        return new LRParemeters(clock, dim, weight.clone(), bias);
    }

    public LRParemeters copyZero() {
        return new LRParemeters(clock, dim, null, 0.0);
    }

    public synchronized int getClock() {
        return clock;
    }

    public synchronized void incrClock() {
        clock++;
    }

    public double[] getWeight() {
        return weight;
    }

    public double getBias() {
        return bias;
    }

    public int getDim() {
        return dim;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public void setWeight(double[] weight) {
        this.weight = weight;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }
}
