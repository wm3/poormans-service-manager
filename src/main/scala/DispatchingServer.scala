package jp.w3ch.psm


import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.Service


class DispatchingServer(dispatch: DispatchingServer.ProxyHandler) extends Service[HttpRequest, HttpResponse] {

  val defaultService = new service.TextResponse("hello")

  override def apply(request: HttpRequest) = {
    val host = Option(request.getHeader("Host"))

    val service = doDispatch(HostUrl(host))

    service(request)
  }


  private[this] val doDispatch: DispatchingServer.ProxyHandler = dispatch orElse {
    case _ => defaultService
  }

}

object DispatchingServer {
  type ProxyHandler = PartialFunction[HostUrl, Service[HttpRequest, HttpResponse]]
}

// vim: set shiftwidth=2 expandtab :
