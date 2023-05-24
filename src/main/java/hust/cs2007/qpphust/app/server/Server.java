package hust.cs2007.qpphust.app.server;

import hust.cs2007.qpphust.app.message.LoginRequestMessage;
import hust.cs2007.qpphust.app.message.LoginResponseMessage;
import hust.cs2007.qpphust.app.protocol.MessageCodecSharable;
import hust.cs2007.qpphust.app.protocol.ProcotolFrameDecoder;
import hust.cs2007.qpphust.app.server.service.UserServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Server
 * PackageName:hust.cs2007.qpphust.app
 * Description:
 * date: 2022/9/29 10:48
 *
 * @author: 邱攀攀
 * @version: since JDK 1.8
 */
@Slf4j(topic = "LoggingHandler")
public class Server {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel sc) throws Exception {
                    sc.pipeline().addLast(new ProcotolFrameDecoder());
                    sc.pipeline().addLast(loggingHandler);
                    sc.pipeline().addLast(messageCodec);
                    sc.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
                            log.debug("接收到一个登陆事件, {}",msg);
                            String username = msg.getUsername();
                            String password = msg.getPassword();
                            LoginResponseMessage responseMessage = null;
                            boolean login = UserServiceFactory.getUserService().login(username, password);
                            if(login){
                                responseMessage = new LoginResponseMessage(true, "登陆成功");
                            }else{
                              responseMessage = new LoginResponseMessage(false, "登陆失败");
                           }
                           ctx.writeAndFlush(responseMessage);
                        }
                   });
                }
            });
            Channel channel = serverBootstrap.bind(8089).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.debug("server error , {}",e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
