package fitz.bigdatalearn.protobuf;

import fitz.bigdatalearn.protobuf.proto.MasterClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9876));

        CMBlockingRpcChannel rpcChannel = new CMBlockingRpcChannel(channel);

        ClientProtocalBP client = new ClientProtocalBP(rpcChannel);

        client.createMatrix("lr", 10, 8);

    }
}
