package jp.w3ch.psm


import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.Service


class DispatchingServer(
    val dispatch: DispatchingServer.ProxyHandler,
    val default: service.HttpService
) extends Service[HttpRequest, HttpResponse] {

  val defaultService = new service.TextResponse("hello")

  override def apply(request: HttpRequest) = {
    val host = Option(request.getHeader("Host"))

    val service = doDispatch(host)

    service(request)
  }


  private[this] def doDispatch(url:Option[String]): service.HttpService = {
    if ( ! url.isDefined) return default

    dispatch.find { _._1(url.get) }
      .map { _._2 }
      .getOrElse(default)
  }

}

object DispatchingServer {
  type ProxyHandler = Seq[(HostUrl, service.HttpService)]

  /**
   * (仮想)ホスト名情報を指すクラスです。
   */
  case class HostUrl(host:String) {
    def apply(name: String): Boolean = {
      name match {
        case HostUrl.re(this.host, _) => true
        case _                        => false
      }
    }
  }

  object HostUrl {

    val re = """([^/:]+)(?::(\d+))?""".r

  }

}

// vim: set shiftwidth=2 expandtab :
