package fitz.multilearn.mllib.logisticregression;

import fitz.multilearn.model.Model;

import java.util.List;


public class LRModel implements Model {
    private LRParemeters params;
    private LRParemeters grads;
    private double[] forward;

    public LRParemeters getParams() {
        return params;
    }

    public void setParams(LRParemeters params) {
        this.params = params;        
    }

    public LRModel(LRParemeters params) {
        this.params = params;
        this.forward = new double[params.getDim()];
        this.grads = params.copyZero();
    }

    public void calObj(List<double[]> dataBatch) {
        double[] weight = params.getWeight();
        double bias = params.getBias();
        int idx = 0;
        for (double[] vector: dataBatch) {
            double dot = 0.0;
            for (int j=0; j<weight.length; j++) {
                dot += weight[j] * vector[j];
            }

            forward[idx] = dot + bias;

            idx++;
        }
    }

    public void calGrad(List<double[]> dataBatch, double[] lossGrad) {
        double[] gradw = new double[params.getDim()];
        double gradb = 0.0;
        for(int i=0; i<gradw.length; i++) {
            gradw[i] = 0.0;
        }

        for (int i =0; i<lossGrad.length; i++) {
            gradb += lossGrad[i];
            double[] vector = dataBatch.get(i);
            for (int j=0; j< params.getDim(); j++) {
                gradw[j] += lossGrad[i]*vector[j];
            }
        }

        for(int i=0; i<gradw.length; i++) {
            gradw[i] /= dataBatch.size();
        }

        gradb /= dataBatch.size();

        grads.setWeight(gradw);
        grads.setBias(gradb);
    }

    public double[] getForward() {
        return forward;
    }

    public LRParemeters getGrad() {
        return grads;
    }

    public int getStatus() {
        return 0;
    }
}
