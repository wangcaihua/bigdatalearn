package fitz.multilearn.paramerer;


public interface Paremeter {
    int getClock();

    void incrClock();

    Paremeter copy();

    Paremeter copyZero();
}
