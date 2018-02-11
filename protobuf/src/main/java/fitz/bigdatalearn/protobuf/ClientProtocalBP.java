package fitz.bigdatalearn.protobuf;


import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.ServiceException;
import fitz.bigdatalearn.protobuf.proto.MasterClient;

import java.util.List;

public class ClientProtocalBP implements CMProtocal {
    private MasterClient.RpcService.BlockingInterface stub;

    public ClientProtocalBP(BlockingRpcChannel channel) {
        this.stub = MasterClient.RpcService.Stub.newBlockingStub(channel);
    }


    public Boolean createMatrix(String name, int rows, int cols) {
        MasterClient.CMRequest req = MasterClient.CMRequest.newBuilder()
                .setName(name).setRows(rows).setCols(cols).build();
        try {
            MasterClient.CMResponse resp = stub.createMatrix(null, req);
            if (null != resp && resp.getStats()) {
                return true;
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean releaseMatrix(String name) {
        MasterClient.RMRequest req = MasterClient.RMRequest.newBuilder().setName(name).build();
        try {
            MasterClient.RMResponse resp = stub.releaseMatrix(null, req);
            if (null != resp && resp.getStats()){
                return true;
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getAllMatrix(String regex) {
        MasterClient.AMRequest req = MasterClient.AMRequest.newBuilder()
                .setName(regex).build();
        try {
            MasterClient.AMResponse resp = stub.getAllMatrix(null, req);
            if (null != resp) {
                return resp.getNamesList();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return null;
    }
}
