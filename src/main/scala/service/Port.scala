package jp.w3ch.psm.service

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http}
import org.jboss.netty.handler.codec.http._

import jp.w3ch.psm.daemon.Daemon


class Port(address:InetSocketAddress) extends HttpService {

  val daemon = Daemon("sh -c 'cd sam; exec node app.js'")

  def this(address: Int) = this(new InetSocketAddress(address))

  def activateDaemon() {
    if ( ! daemon.isRunning && true) daemon.exec()
  }

  override def apply(request:HttpRequest) = {

    activateDaemon()

    val client = ClientBuilder()
      .codec(Http())
      .hosts(address)
      .hostConnectionLimit(1)
      .build()

    client(request) onSuccess { response =>
      response
    } ensure {
      client.release()
    }
  }
}

// vim: set ts=2 sw=2 et:
