package jp.w3ch.psm


import scala.io.Source

import java.net.InetSocketAddress

import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.Http

import com.twitter.conversions.time._
import com.twitter.util.Eval


object Launcher extends App {

  val loader = new Eval().apply[Configuration](Source.fromFile("config").mkString)
  //val server = loader.load()

  val sb = ServerBuilder()
    .name   ("httpserver")
    .codec  (Http())
    .bindTo (new InetSocketAddress(3000))

  sb.maxConcurrentRequests     (2)
    .hostConnectionMaxLifeTime (5.minutes)
    .readTimeout               (2.minutes)


  val server = sb.build(loader())

  System.in.read();
  server.close(4.seconds)

}
// vim: set shiftwidth=2 expandtab :
