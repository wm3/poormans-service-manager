package jp.w3ch.psm.config

import jp.w3ch.psm.service


trait ConfigurationUtil {

  val port = new service.Port(_:Int)
  val daemon = new service.Daemon(_:String, _:Int)
  val textResponse = new service.TextResponse(_)
}

// vim: set shiftwidth=2 expandtab :
