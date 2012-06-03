package jp.w3ch.psm


import java.net.InetSocketAddress

import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

import com.twitter.finagle.Service


object MyServer {
  type ProxyHandler = PartialFunction[HostUrl, Service[HttpRequest, HttpResponse]]
}

class MyServer(proxy: MyServer.ProxyHandler) extends Service[HttpRequest, HttpResponse] {

  val defaultServer = new service.TextResponse("hello")
  val getDefaultServer: MyServer.ProxyHandler = { case _ => defaultServer }

  override def apply(request: HttpRequest) = {
    val host = Option(request.getHeader("Host"))

    val server = (proxy orElse getDefaultServer)(HostUrl(host))

    server(request)
  }
}
// vim: set shiftwidth=2 expandtab :
