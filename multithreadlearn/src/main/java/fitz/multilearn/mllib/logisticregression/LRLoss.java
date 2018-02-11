package fitz.multilearn.mllib.logisticregression;

import fitz.multilearn.loss.Loss;
import fitz.multilearn.model.Model;

/**
 * Created by Administrator on 2018/2/11 0011.
 */
public class LRLoss implements Loss{
    public double calLoss(Model model, double[] groundtruth) {
        double[] foeward = model.getForward();
        double loss = 0.0;
        for (int i=0; i< groundtruth.length; i++) {
            loss += Math.log1p(Math.exp(-foeward[i]*groundtruth[i]));
        }
        return loss;
    }

    public double[] calLossGrad(Model model, double[] groundtruth) {
        double[] forward = model.getForward();
        double[] lossGrad = new double[groundtruth.length];

        for (int i=0; i< groundtruth.length; i++) {
            lossGrad[i] = -forward[i] / (1.0 + Math.exp(forward[i]*groundtruth[i]));
        }

        return lossGrad;
    }
}
