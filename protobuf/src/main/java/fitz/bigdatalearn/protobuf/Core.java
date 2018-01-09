package fitz.bigdatalearn.protobuf;

import com.google.protobuf.RpcCallback;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import fitz.bigdatalearn.protobuf.proto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Core {
    private static Logger log = LoggerFactory.getLogger(Server.class);

    // 回调
    private static final RpcCallback<Message.Msg> responseCallback =
            new RpcCallback<Message.Msg>() {
                public void run(Message.Msg parameter) {
                    log.info("接收  " + parameter);
                }
            };

    public static RpcClientChannel channel = null;

    public static RpcConnectionEventNotifier getNotifier(RpcConnectionEventListener listener) {
        // 启动rpc事件监听
        RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
        rpcEventNotifier.setEventListener(listener);
        return rpcEventNotifier;
    }

    public static RpcConnectionEventListener getServerListener() {
        RpcConnectionEventListener listener = new RpcConnectionEventListener() {
            public void connectionReestablished(RpcClientChannel clientChannel) {
                log.info("重新建立连接 " + clientChannel);
                clientChannel.setOobMessageCallback(Message.Msg.getDefaultInstance(), responseCallback);
            }

            public void connectionOpened(RpcClientChannel clientChannel) {
                log.info("链接打开" + clientChannel);
                clientChannel.setOobMessageCallback(Message.Msg.getDefaultInstance(), responseCallback);
            }

            public void connectionLost(RpcClientChannel clientChannel) {
                log.info("链接断开" + clientChannel);
            }

            public void connectionChanged(RpcClientChannel clientChannel) {
                log.info("链接改变" + clientChannel);
            }
        };

        return listener;
    }

    public static RpcConnectionEventListener getClientListener() {
        RpcConnectionEventListener listener = new RpcConnectionEventListener() {
            public void connectionReestablished(RpcClientChannel clientChannel) {
                channel = clientChannel;
                log.info("重新建立连接 " + clientChannel);
                clientChannel.setOobMessageCallback(Message.Msg.getDefaultInstance(), responseCallback);
            }

            public void connectionOpened(RpcClientChannel clientChannel) {
                channel = clientChannel;
                log.info("链接打开" + clientChannel);
                clientChannel.setOobMessageCallback(Message.Msg.getDefaultInstance(), responseCallback);
            }

            public void connectionLost(RpcClientChannel clientChannel) {
                log.info("链接断开" + clientChannel);
            }

            public void connectionChanged(RpcClientChannel clientChannel) {
                channel = clientChannel;
                log.info("链接改变" + clientChannel);
            }
        };
        return listener;
    }
}