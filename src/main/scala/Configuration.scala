package jp.w3ch.psm

import java.net.InetSocketAddress
import com.twitter.finagle.http.Http
import com.twitter.finagle.builder.{Server, ServerBuilder}

import com.twitter.conversions.time._

import org.jboss.netty.handler.codec.http._

class Configuration extends com.twitter.util.Config[Server] with ConfigurationUtil {
  // parameters
  var listen = required[Int]
  var proxyHandler = required[MyServer.ProxyHandler]

  def proxy:MyServer.ProxyHandler = proxyHandler
  def proxy_=(proxy: MyServer.ProxyHandler) { proxyHandler = proxy }


  // building process

  override def apply = {
    val sb = ServerBuilder()
      .name   ("httpserver")
      .codec  (Http())
      .bindTo (new InetSocketAddress(listen))

    sb.maxConcurrentRequests     (2)
      .hostConnectionMaxLifeTime (5.minutes)
      .readTimeout               (2.minutes)

    sb.build(new MyServer(proxyHandler))
  }
}

trait ConfigurationUtil {
  val nullProxyHandler: MyServer.ProxyHandler = { case _ if false => throw new Exception() }
  val port = Services.proxyThat(_)
  val textResponse = Services.textResponse(_)
}


// 

sealed class HostUrl {}
case class Host(host:String, port:Int) extends HostUrl {}
object NoHost extends HostUrl

object HostUrl {
  val re = """([^/:]+)(?::(\d+))?""".r
  def get(s: String): Option[String] = { if (s == "") None else Some(s) }

  def apply(name: Option[String]): HostUrl = {
    name match {
      case Some(re(host, port)) => Host(host, get(port).map(_.toInt).getOrElse(80))
      case _ => NoHost
    }
  }
}

// vim: set shiftwidth=2 expandtab :
