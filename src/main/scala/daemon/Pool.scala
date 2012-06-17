package jp.w3ch.psm.daemon


/**
 * 各種プロセスを管理します。
 */
class Pool(daemons: Seq[Daemon]) {

  def stop() {
    daemons.foreach { _.stop() }
  }

}

// vim: set shiftwidth=2 expandtab:
