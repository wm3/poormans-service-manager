package jp.w3ch.psm.service

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

import com.twitter.util.Future
import com.twitter.finagle.http.{Response}
import org.jboss.netty.handler.codec.http._


class TextResponse(prefix:String) extends HttpService {

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

// vim: set ts=2 sw=2 et:
