package jp.w3ch.psm


import java.io.File

import com.twitter.conversions.time._
import com.twitter.util.Eval


/**
 * main program
 */
object Psm {

  val configPath = "config"

  def main(args:Array[String]) {
    var config = new Eval().apply[Configuration](new File(configPath))
    val server = {
      config()
    }

    println("Listening on port " + config.listen.toInt + ". Press any key to quit.")
    System.in.read()

    server.close(4.seconds)
  }
}

// vim: set shiftwidth=2 expandtab :
