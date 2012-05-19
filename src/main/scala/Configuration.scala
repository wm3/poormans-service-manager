package jp.w3ch.psm


import org.jboss.netty.handler.codec.http._

import com.twitter.util.Eval
import com.twitter.finagle.Service

class Configuration extends com.twitter.util.Config[MyServer] with ConfigurationUtil {
  var proxyHandler = required[MyServer.ProxyHandler]

  def proxy:MyServer.ProxyHandler = proxyHandler
  def proxy_=(proxy: MyServer.ProxyHandler) { proxyHandler = proxy }

  override def apply = new MyServer(proxyHandler)
}

trait ConfigurationUtil {
  val nullProxyHandler: MyServer.ProxyHandler = { case _ if false => throw new Exception() }
  val proxyThat = Services.proxyThat(_)
  val textResponse = Services.textResponse(_)
}

trait ConfigurationLoader {

  def load():Option[Configuration]
}


class FileConfigurationLoader(file: String) extends ConfigurationLoader {

  import scala.io.Source

  override def load() = {
    try {
      Some(new Eval()(Source.fromFile(file).mkString))
    } catch {
      case e:Exception =>
        println(e)
        None
    }
  }

}


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
