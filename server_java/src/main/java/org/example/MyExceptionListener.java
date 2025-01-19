package org.example;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DefaultExceptionListener;
import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MyExceptionListener extends DefaultExceptionListener {
    private static final Logger log = LoggerFactory.getLogger(MyExceptionListener.class);


    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        return true;
    }

}
