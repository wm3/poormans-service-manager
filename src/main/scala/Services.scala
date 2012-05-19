package jp.w3ch.psm


import java.net.InetSocketAddress

import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

import com.twitter.util.Future
import com.twitter.finagle.http.{Http,Response}

import com.twitter.finagle.Service


object Services {

  type S = Service[HttpRequest, HttpResponse]

  def textResponse(prefix:String): S = new TextResponse(prefix)
  def proxyThat(address:Int): S = new ProxyThat(new InetSocketAddress(address))

  private[this] class TextResponse(prefix:String) extends S {

    import java.text.SimpleDateFormat
    import java.util.Locale
    import java.util.Date

    override def apply(request:HttpRequest) = {

      val host = Option(request.getHeader("Host")) getOrElse ""
      val text = prefix + " " + host

      Future.value(makeResponse(text))
    }

    val dateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US)

    def now = dateFormat.format(new Date())

    def makeResponse(text: String):HttpResponse = {

      import org.jboss.netty.buffer.ChannelBuffers
      import org.jboss.netty.util.CharsetUtil.UTF_8

      val bytes = text.getBytes(UTF_8)
      val body = ChannelBuffers.copiedBuffer(bytes)
      val headers = Seq(
        "Date"           -> now,
        "Content-Length" -> bytes.length.toString,
        "Connection"     -> "close",
        "Content-Type"   -> "text/plain"
      )

      val response = Response()
      response.setContent(body)
      headers.foreach { kv => response.addHeader(kv._1, kv._2) }

      response
    }
  }

  private[this] class ProxyThat(address:InetSocketAddress) extends S {

    import jp.w3ch.psm.daemon.Daemon
    import com.twitter.finagle.builder.ClientBuilder

    val daemon = Daemon("sh -c 'cd sam; node app.js'")

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
}

// vim: set shiftwidth=2 expandtab :
