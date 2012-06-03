package jp.w3ch.psm

import java.net.InetSocketAddress
import com.twitter.finagle.http.Http
import com.twitter.finagle.builder.{Server, ServerBuilder}

import com.twitter.conversions.time._

import org.jboss.netty.handler.codec.http._

class Configuration extends com.twitter.util.Config[Server] with ConfigurationUtil {

  // ----------------------------------------------------------------
  //     Parameters
  // ----------------------------------------------------------------

  var listen = required[Int]
  var proxyHandler = required[DispatchingServer.ProxyHandler]

  def proxy:DispatchingServer.ProxyHandler = proxyHandler
  def proxy_=(proxy: DispatchingServer.ProxyHandler) { proxyHandler = proxy }


  // ----------------------------------------------------------------
  //     Building process
  // ----------------------------------------------------------------

  override def apply = {
    val sb = ServerBuilder()
      .name   ("httpserver")
      .codec  (Http())
      .bindTo (new InetSocketAddress(listen))

    sb.maxConcurrentRequests     (2)
      .hostConnectionMaxLifeTime (5.minutes)
      .readTimeout               (2.minutes)

    sb.build(new DispatchingServer(proxyHandler))
  }
}


// ----------------------------------------------------------------
//     Utilities
// ----------------------------------------------------------------

trait ConfigurationUtil {
  val nullProxyHandler: DispatchingServer.ProxyHandler = { case _ if false => throw new Exception() }
  val port = new service.Port(_:Int)
  val daemon = new service.Daemon(_:String, _:Int)
  val textResponse = new service.TextResponse(_)
}


/**
 * (仮想)ホスト名情報を指すクラスです。
 */
sealed class HostUrl {}
case class Host(host:String, port:Int) extends HostUrl {}
object NoHost extends HostUrl

object HostUrl {

  private[this] val re = """([^/:]+)(?::(\d+))?""".r

  private[this] def get(s: String): Option[String] = { if (s == "") None else Some(s) }

  /**
   * 文字列からホスト情報を作成します。
   */
  def apply(name: Option[String]): HostUrl = {
    name match {
      case Some(re(host, port)) => Host(host, get(port).map(_.toInt).getOrElse(80))
      case _ => NoHost
    }
  }
}

// vim: set shiftwidth=2 expandtab :
