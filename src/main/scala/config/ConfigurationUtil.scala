package jp.w3ch.psm.config

import jp.w3ch.psm.service
import jp.w3ch.psm.daemon.Daemon


trait ConfigurationUtil {

  def addDaemon(d:Daemon):Unit

  val port = new service.Port(_:Int)
  val textResponse = new service.TextResponse(_)

  def daemon(command:String, port:Int) = {
    val d = Daemon(command)
    addDaemon(d)

    new service.Daemon(d, port)
  }
}

// vim: set shiftwidth=2 expandtab :
