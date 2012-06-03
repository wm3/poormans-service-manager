package jp.w3ch.psm.service

import java.net.InetSocketAddress

import com.twitter.conversions.time._
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.service.{Backoff,RetryPolicy}
import com.twitter.finagle.http.{Http,Request,Method}
import com.twitter.util.{Future,Promise,Time}
import org.jboss.netty.handler.codec.http._

import jp.w3ch.psm.daemon
import jp.w3ch.psm.util.Timer;

class Daemon(command:String, address:InetSocketAddress) extends HttpService {
  var lastRequestedAt = Time.now

  val stopTimer = Timer.schedule(5.seconds) {
    if(lastRequestedAt + 5.minutes < Time.now) {
      executor.stop()
    }
  }

  val executor = daemon.Daemon(command)
  val port = new Port(address)

  def exec = executor.exec()

  def this(command:String, address: Int) = this(command, new InetSocketAddress("127.0.0.1", address))

  override def apply(request:HttpRequest) = {
    lastRequestedAt = Time.now
    for {
      _ <- waitForPort(1)
             .rescue {
               case _ => Future(exec).join(waitForPort(4))
             }
      response <- port(request)
    } yield(response)
  }

  def waitForPort(retry:Int) = {
    val client = ClientBuilder()
      .codec(Http())
      .hosts(address)
      .hostConnectionLimit(1)
      .retryPolicy(
        RetryPolicy.backoff(Backoff.const(500.milliseconds).take(retry - 1))(RetryPolicy.WriteExceptionsOnly))
      .build()

    client(Request(Method.Head, "/")).unit
  }
}

// vim: set ts=2 sw=2 et:
