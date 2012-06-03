package jp.w3ch.psm


import java.net.InetSocketAddress

import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

import com.twitter.finagle.Service


object DispatchingServer {
  type ProxyHandler = PartialFunction[HostUrl, Service[HttpRequest, HttpResponse]]
}

class DispatchingServer(proxy: DispatchingServer.ProxyHandler) extends Service[HttpRequest, HttpResponse] {

  val defaultServer = new service.TextResponse("hello")
  val getDefaultServer: DispatchingServer.ProxyHandler = { case _ => defaultServer }

  override def apply(request: HttpRequest) = {
    val host = Option(request.getHeader("Host"))

    val server = (proxy orElse getDefaultServer)(HostUrl(host))

    server(request)
  }
}
// vim: set shiftwidth=2 expandtab :
