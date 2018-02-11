package fitz.multilearn.model;


import fitz.multilearn.paramerer.Paremeter;

import java.util.List;

public interface Model {

    void calObj(List<double[]> dataBatch);

    void calGrad(List<double[]> dataBatch, double[] lossGrad);

    double[] getForward();

    Paremeter getGrad();

    int getStatus();

}
