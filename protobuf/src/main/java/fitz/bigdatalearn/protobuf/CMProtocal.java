package fitz.bigdatalearn.protobuf;


import java.util.List;

public interface CMProtocal {
    Boolean createMatrix(String name, int rows, int cols);
    Boolean releaseMatrix(String name);
    List<String> getAllMatrix(String regex);
}
