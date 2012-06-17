package jp.w3ch.psm.config

import java.net.InetSocketAddress
import scala.collection.mutable

import com.twitter.finagle.http.Http
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.conversions.time._
import org.jboss.netty.handler.codec.http._

import jp.w3ch.psm.service.HttpService
import jp.w3ch.psm.DispatchingServer


class Configuration extends com.twitter.util.Config[Server] with ConfigurationUtil {

  // ----------------------------------------------------------------
  //     Parameters
  // ----------------------------------------------------------------

  var listen = required[Int]
  var defaultProxy = required[HttpService]
  val proxyHandler = mutable.Buffer[DispatchingServer.ProxyEntry]()


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

    sb.build(new DispatchingServer(proxyHandler.readOnly, defaultProxy))
  }


  // ----------------------------------------------------------------
  //     Proxy settings
  // ----------------------------------------------------------------

  def when(host:String) = new ProxyCondition(DispatchingServer.HostUrl(host))
  class ProxyCondition(hostUrl: DispatchingServer.HostUrl) {
    def ->(service:HttpService) {
      proxyHandler += ((hostUrl, service))
    }
  }

  object default {
    def ->(service:HttpService) {
      defaultProxy = service
    }
  }

}


// vim: set shiftwidth=2 expandtab :
