package fitz.learn.quant

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel


class Client(host:String, port: Int) {
  val workers= new NioEventLoopGroup

  def run(): Unit = {
    val bootstrap = new Bootstrap()

    bootstrap.group(workers)
    bootstrap.channel(classOf[NioSocketChannel])
    bootstrap.handler(new ClientHandler())

    bootstrap.connect(host, port)
  }
}

object Client {
  def main(args: Array[String]): Unit = {
    val client = new Client("127.0.0.1", 1111)
    client.run()
  }
}
