package fitz.multilearn.loss;

import fitz.multilearn.model.Model;


public interface Loss {

    double calLoss(Model model, double[] groundtruth);

    double[] calLossGrad(Model model, double[] groundtruth);
}
