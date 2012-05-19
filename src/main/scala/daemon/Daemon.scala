package jp.w3ch.psm.daemon

/**
 * 1つのプロセスを管理します。
 */
class Daemon(executor: (() => Instance)) {

  var instance: Option[Instance] = None

  def exec() {
    if (instance.isDefined) throw new IllegalStateException()

    instance = Some(executor())
  }

  def stop() {
    if (isRunning) {
      instance.get.stop()
      instance = None
    }
  }

  def isRunning:Boolean = {
    instance match {
      case Some(p) => p.isRunning
      case None => false
    }
  }

}

object Daemon {
  def apply(command:String): Daemon = new Daemon(() => new SimpleInstance(command))
}


private class SimpleInstance(command:String) extends Instance {

  import scala.sys.process._

  var pid: Option[String] = None

  val fullCommand = Seq("sh", "-c", "echo $$; exec " + command)

  val process = fullCommand.run(logger)


  override def stop() {
    process.destroy()
  }

  override def isRunning: Boolean = {
    val ps = Seq("ps", "-p", pid.get)
    val grep = Seq("grep", pid.get)

    var hasPid = false
    (ps #| grep).run(ProcessLogger(l => hasPid = true, {l => })).exitValue

    hasPid
  }

  def logger: ProcessLogger = {
    val processFirstLineAndRest = { (line:String) =>
      if (pid.isEmpty) pid = Some(line)
      else println(line)
    }
    ProcessLogger(processFirstLineAndRest, println(_))
  }
}

// vim: set shiftwidth=2 expandtab :
