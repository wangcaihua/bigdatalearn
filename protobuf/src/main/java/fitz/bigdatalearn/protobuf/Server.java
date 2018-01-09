package fitz.bigdatalearn.protobuf;

import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;
import fitz.bigdatalearn.protobuf.proto.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;

public class Server {
    private static Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        // 配置server
        PeerInfo serverInfo = new PeerInfo("localhost", 1234);
        DuplexTcpServerPipelineFactory serverFactory = new DuplexTcpServerPipelineFactory(serverInfo);

        // 设置线程池
        RpcServerCallExecutor rpcExecutor = new ThreadPoolCallExecutor(10, 10);
        serverFactory.setRpcServerCallExecutor(rpcExecutor);

        // 关闭 减少日志
        CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
        logger.setLogRequestProto(false);
        logger.setLogResponseProto(false);

        // 设置rpc事件监听
        serverFactory.registerConnectionEventListener(Core.getNotifier(Core.getServerListener()));

        //初始化netty
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(2,
                new RenamingThreadFactoryProxy("boss",
                Executors.defaultThreadFactory()));
        EventLoopGroup workers = new NioEventLoopGroup(16,
                new RenamingThreadFactoryProxy("worker",
                Executors.defaultThreadFactory()));
        bootstrap.group(boss, workers);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 1048576);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 1048576);
        //bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        bootstrap.childHandler(serverFactory);
        bootstrap.localAddress(serverInfo.getPort());

        //关闭释放资源
        CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(boss);
        shutdownHandler.addResource(workers);
        shutdownHandler.addResource(rpcExecutor);

        bootstrap.bind();
        log.info("启动监听： " + bootstrap);

        //定时向客户端发送消息
        while (true) {
            List<RpcClientChannel> clients = serverFactory.getRpcClientRegistry().getAllClients();
            for (RpcClientChannel client : clients) {
                //创建消息
                Message.Msg msg = Message.Msg.newBuilder().setContent(
                        "Server "+ serverFactory.getServerInfo() + " OK@" + System.currentTimeMillis()).build();

                ChannelFuture oobSend = client.sendOobMessage(msg);
                if (!oobSend.isDone()) {
                    log.info("Waiting for completion.");
                    oobSend.syncUninterruptibly();
                }
                if (!oobSend.isSuccess()) {
                    log.warn("OobMessage send failed." + oobSend.cause());
                }

            }
            log.info("Sleeping 5s before sending request to all clients.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}