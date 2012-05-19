package jp.w3ch.psm


import java.net.InetSocketAddress

import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.Http

import com.twitter.conversions.time._


object Launcher extends App {

  val sb = ServerBuilder()
    .name   ("httpserver")
    .codec  (Http())
    .bindTo (new InetSocketAddress(3000))

  sb.maxConcurrentRequests     (2)
    .hostConnectionMaxLifeTime (5.minutes)
    .readTimeout               (2.minutes)
    //.requestTimeout            (2.seconds)
    //.keepAlive                 (true)


  val loader = new FileConfigurationLoader("config")

  val server = sb.build(loader.load().get())

  System.in.read();
  server.close(4.seconds)

}
// vim: set shiftwidth=2 expandtab :
