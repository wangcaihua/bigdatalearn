package fitz.bigdatalearn.protobuf;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import fitz.bigdatalearn.protobuf.proto.MasterClient;

import java.util.List;

/**
 * Created by Administrator on 2018/1/28 0028.
 */
public class MsaterProcolBP implements MasterClient.RpcService.BlockingInterface {
    CMProtocal protocal;

    public MsaterProcolBP(CMProtocal protocal) {
        this.protocal = protocal;
    }

    public MasterClient.CMResponse createMatrix(RpcController controller, MasterClient.CMRequest request) throws ServiceException {
        Boolean stats = protocal.createMatrix(request.getName(), request.getRows(), request.getCols());
        return MasterClient.CMResponse.newBuilder().setStats(stats).build();
    }

    public MasterClient.RMResponse releaseMatrix(RpcController controller, MasterClient.RMRequest request) throws ServiceException {
        Boolean stats = protocal.releaseMatrix(request.getName());
        return MasterClient.RMResponse.newBuilder().setStats(stats).build();
    }

    public MasterClient.AMResponse getAllMatrix(RpcController controller, MasterClient.AMRequest request) throws ServiceException {
        List<String> matrices = protocal.getAllMatrix(request.getName());

        MasterClient.AMResponse.Builder builder = MasterClient.AMResponse.newBuilder();

        for (int i =0; i < matrices.size(); i++) {
            builder.setNames(i, matrices.get(i));
        }

        return builder.build();
    }
}
