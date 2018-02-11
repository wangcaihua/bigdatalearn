package fitz.bigdatalearn.protobuf;

import com.google.protobuf.*;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

// serviceId, methodId
public class CMBlockingRpcChannel implements BlockingRpcChannel {
    SocketChannel channel;

    public static int flag = 9856363;

    public CMBlockingRpcChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public Message callBlockingMethod(
            Descriptors.MethodDescriptor method,
            RpcController controller,
            Message request,
            Message responsePrototype) throws ServiceException {
        int serviceId = method.getService().getIndex();
        int methodId = method.getIndex();
        int requestLen = request.getSerializedSize();

        int bufferLen = requestLen + 4*4;
        ByteBuffer wbb = ByteBuffer.allocate(bufferLen);
        ByteBuffer rbb = ByteBuffer.allocate(2048);
        wbb.putInt(flag).putInt(serviceId).putInt(methodId)
                .putInt(requestLen).put(request.toByteArray());


        try {
            channel.write(wbb);

            rbb.clear();
            channel.read(rbb);
            return responsePrototype.getParserForType().parseFrom(rbb.array());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
