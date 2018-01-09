package fitz.bigdatalearn.protobuf;


import fitz.bigdatalearn.protobuf.proto.PersonAddressBook.AddressBook;

import fitz.bigdatalearn.protobuf.proto.PersonAddressBook.Person;
import fitz.bigdatalearn.protobuf.proto.PersonAddressBook.Person.PhoneNumber;
import fitz.bigdatalearn.protobuf.proto.PersonAddressBook.PhoneType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import fitz.bigdatalearn.protobuf.proto.Message;

import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.client.RpcClientConnectionWatchdog;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class Client {
    private static Person getPerson(String name, String num, String email) {
        PhoneNumber PhoneNum1 = PhoneNumber.newBuilder()
                .setNumber(num)
                .setType(PhoneType.HOME)
                .build();
        PhoneNumber PhoneNum2 = PhoneNumber.newBuilder()
                .setNumber(num)
                .setType(PhoneType.WORK)
                .build();

        Person.Builder pb = Person.newBuilder();
        pb.setName(name)
                .setEmail(email)
                .setId(0)
                .addPhone(PhoneNum1)
                .addPhone(PhoneNum2);

        return pb.build();
    }

    private static AddressBook getAddressBook() {
        AddressBook.Builder ab = AddressBook.newBuilder();
        ab.addPerson(getPerson("Fitz", "15997584532", "Fitz@163.com"));
        ab.addPerson(getPerson("Wch", "15997584462", "Wch@163.com"));
        ab.addPerson(getPerson("ZW", "15997584529", "ZW@qq.com"));
        return ab.build();
    }

    private static void serDeser() {
        Person p = getPerson("Fitz", "15997584532", "Fitz@163.com");
        getAddressBook();
        System.out.println(p.toString());

        try {
            FileOutputStream fo = new FileOutputStream("person.bin");
            p.writeTo(fo);
            fo.flush();
            fo.close();
        } catch (IOException e) {
            System.out.println("ERROR-1 !");
        }

        System.out.println();

        try {
            FileInputStream fi = new FileInputStream("person.bin");
            Person p2 = Person.parseFrom(fi);
            System.out.println(p2.toString());
            fi.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR-2 !");
        } catch (IOException e) {
            System.out.println("ERROR-3 !");
        }
    }

    private static Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        PeerInfo server = new PeerInfo("localhost", 1234);
        DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory();
        //设置响应超时时间
        clientFactory.setConnectResponseTimeoutMillis(10000);
        clientFactory.setRpcServerCallExecutor(new ThreadPoolCallExecutor(3, 10));
        //打开数据压缩
        clientFactory.setCompression(true);

        // RPC payloads are uncompressed when logged - so reduce logging
        CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
        logger.setLogRequestProto(false);
        logger.setLogResponseProto(false);
        clientFactory.setRpcLogger(logger);

        // 设置rpc事件监听
        RpcConnectionEventNotifier rpcEventNotifier = Core.getNotifier(Core.getClientListener());
        clientFactory.registerConnectionEventListener(rpcEventNotifier);

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workers = new NioEventLoopGroup(16,
                new RenamingThreadFactoryProxy("workers",
                        Executors.defaultThreadFactory()));
        bootstrap.group(workers);
        bootstrap.handler(clientFactory);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);

        RpcClientConnectionWatchdog watchdog = new RpcClientConnectionWatchdog(
                clientFactory, bootstrap);
        rpcEventNotifier.addEventListener(watchdog);
        watchdog.start();

        CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(workers);

        clientFactory.peerWith(server, bootstrap);

        while (Core.channel != null) {
            //创建消息
            Message.Msg msg = Message.Msg.newBuilder().setContent("Client " + Core.channel + " OK@" + System.currentTimeMillis()).build();
            ChannelFuture oobSend = Core.channel.sendOobMessage(msg);
            if (!oobSend.isDone()) {
                log.info("Waiting for completion.");
                oobSend.syncUninterruptibly();
            }
            if (!oobSend.isSuccess()) {
                log.warn("OobMessage send failed." + oobSend.cause());
            }

            try {
                log.info("Sleeping 5s before sending request to server.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.exit(0);
    }
}
