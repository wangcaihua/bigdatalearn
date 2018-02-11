package fitz.bigdatalearn.protobuf;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.ServiceException;
import fitz.bigdatalearn.protobuf.proto.MasterClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class Server {

    public static void main(String[] args) throws IOException, ServiceException {
        CMProtocal cmProtocal = new CMProtocalImpl();
        MsaterProcolBP msaterProcolBP = new MsaterProcolBP(cmProtocal);

        BlockingService server = MasterClient.RpcService.newReflectiveBlockingService(msaterProcolBP);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9876));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            ByteBuffer rbb = ByteBuffer.allocate(2048);

            rbb.clear();
            socketChannel.read(rbb);

            int flag = rbb.getInt();
            int serviceId = rbb.getInt();
            int methodId = rbb.getInt();
            int requestLen = rbb.getInt();
            byte[] dst = new byte[requestLen];
            rbb.get(dst);

            Descriptors.MethodDescriptor method = MasterClient.getDescriptor()
                    .getServices().get(serviceId)
                    .getMethods().get(methodId);


            Message requestPrototype = server.getRequestPrototype(method);
            Message request = requestPrototype.getParserForType().parseFrom(dst);

            Message response = server.callBlockingMethod(method, null, request);

            socketChannel.write(ByteBuffer.wrap(response.toByteArray()));
            socketChannel.close();
        }

    }
}
