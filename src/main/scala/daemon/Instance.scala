package jp.w3ch.psm.daemon

trait Instance {
  def stop():Unit
  def isRunning:Boolean
}
