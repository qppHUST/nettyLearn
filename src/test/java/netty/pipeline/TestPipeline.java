package netty.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * ClassName: TestPipeline
 * PackageName:netty.pipeline
 * Description:
 * date: 2022/4/15 14:35
 *
 * @author: 邱攀攀
 * @version:
 * @since JDK 1.8
 */
@Slf4j(topic = "LoggingHandler")
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();

                        pipeline.addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                        log.debug("channel register");
                                        super.channelRegistered(ctx);
                                    }
                                })
                                .addLast("h1",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("1");
                                        String s = ((ByteBuf) msg).toString();
                                        super.channelRead(ctx, s);
                                    }
                                })
                                .addLast("h2",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("2");
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast("h3",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("3");
                                        nioSocketChannel.writeAndFlush(ctx.alloc().buffer().writeBytes("sasasa ".getBytes(StandardCharsets.UTF_8)));
                                    }
                                })
                                .addLast("h4",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("4");
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast("h5",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("5");
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast("h6",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("6");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                })
                .bind(8080);
    }
}
