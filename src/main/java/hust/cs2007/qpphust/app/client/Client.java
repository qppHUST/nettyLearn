package hust.cs2007.qpphust.app.client;

import hust.cs2007.qpphust.app.message.LoginRequestMessage;
import hust.cs2007.qpphust.app.protocol.MessageCodecSharable;
import hust.cs2007.qpphust.app.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * ClassName: Client
 * PackageName:hust.cs2007.qpphust.app
 * Description:
 * date: 2022/9/29 10:48
 *
 * @author: 邱攀攀
 * @version: since JDK 1.8
 */
@Slf4j(topic = "LoggingHandler")
public class Client  {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        CountDownLatch WAITING_FOR_LOGIN = new CountDownLatch(1);
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel sc) throws Exception {
                    sc.pipeline().addLast(new ProcotolFrameDecoder());//桢解码器
                    sc.pipeline().addLast(loggingHandler);
//                    sc.pipeline().addLast(messageCodecSharable);
                    sc.pipeline().addLast("Client Handler",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg : {}",msg);
                            WAITING_FOR_LOGIN.countDown();
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            group.submit(()->{
                                Scanner sc = new Scanner(System.in);
                                System.out.println("输入用户名：");
                                String name = sc.nextLine();
                                System.out.println("输入密码: ");
                                String password = sc.nextLine();
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(name, password);
                                ChannelFuture channelFuture = ctx.writeAndFlush(loginRequestMessage);
                                try {
                                    WAITING_FOR_LOGIN.wait();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8089).sync().channel();
//            channel.writeAndFlush(new LoginRequestMessage("zhangsan","123"));
            ChannelFuture sync = channel.closeFuture().sync();

        }catch (Exception e){
            log.debug("Client error , {}",e);
        }finally{
            group.shutdownGracefully();
        }
    }
}
