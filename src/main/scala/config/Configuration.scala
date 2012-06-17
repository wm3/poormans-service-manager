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

  import DispatchingServer._

  // ----------------------------------------------------------------
  //     Parameters
  // ----------------------------------------------------------------

  var listen = required[Int]
  var defaultProxy = required[HttpService]
  val proxyHandler = mutable.Buffer[(HostUrl, HttpService)]()


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

  def when(host:String) = new ProxyCondition((HostUrl(host), _))
  class ProxyCondition(makePartial: HttpService => (HostUrl, HttpService)) {
    def ->(service:HttpService) {
      proxyHandler += makePartial(service)
    }
  }

  object default {
    def ->(service:HttpService) {
      defaultProxy = service
    }
  }

}


// vim: set shiftwidth=2 expandtab :
