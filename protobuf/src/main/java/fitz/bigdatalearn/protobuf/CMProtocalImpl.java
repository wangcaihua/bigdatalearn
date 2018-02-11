package fitz.bigdatalearn.protobuf;

import java.util.List;


public class CMProtocalImpl implements CMProtocal {

    public Boolean createMatrix(String name, int rows, int cols) {
        System.out.println(name + "\t" + rows + "\t" + cols);
        return null;
    }

    public Boolean releaseMatrix(String name) {
        return null;
    }

    public List<String> getAllMatrix(String regex) {
        return null;
    }
}
