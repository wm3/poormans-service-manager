package jp.w3ch.psm


import scala.io.Source

import com.twitter.conversions.time._
import com.twitter.util.Eval


object Launcher extends App {

  val loader = new Eval().apply[Configuration](Source.fromFile("config").mkString)

  val server = loader()

  System.in.read();
  server.close(4.seconds)

}
// vim: set shiftwidth=2 expandtab :
