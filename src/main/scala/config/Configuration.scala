package jp.w3ch.psm.config

import java.net.InetSocketAddress
import com.twitter.finagle.http.Http
import com.twitter.finagle.builder.{Server, ServerBuilder}

import com.twitter.conversions.time._

import org.jboss.netty.handler.codec.http._

import jp.w3ch.psm.DispatchingServer


class Configuration extends com.twitter.util.Config[Server] with ConfigurationUtil {

  import DispatchingServer._

  // ----------------------------------------------------------------
  //     Parameters
  // ----------------------------------------------------------------

  var listen = required[Int]
  var defaultProxy = required[Proxy]
  val proxyHandler = scala.collection.mutable.Buffer[(HostUrl, Proxy)]()


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
  //     Building process
  // ----------------------------------------------------------------

  def when(host:String) = new ProxyCondition((HostUrl(host), _))
  class ProxyCondition(makePartial: Proxy => (HostUrl, Proxy)) {
    def ->(service:Proxy) {
      proxyHandler += makePartial(service)
    }
  }

  object default {
    def ->(service:Proxy) {
      defaultProxy = service
    }
  }

}


// ----------------------------------------------------------------
//     Utilities
// ----------------------------------------------------------------

trait ConfigurationUtil {

  import jp.w3ch.psm.service

  val port = new service.Port(_:Int)
  val daemon = new service.Daemon(_:String, _:Int)
  val textResponse = new service.TextResponse(_)
}



// vim: set shiftwidth=2 expandtab :
