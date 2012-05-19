package jp.w3ch.psm.service

import java.net.InetSocketAddress

import org.jboss.netty.handler.codec.http._

import jp.w3ch.psm.daemon


class Daemon(command:String, address:InetSocketAddress) extends HttpService {

  val executor = daemon.Daemon(command)
  val port = new Port(address)

  def this(command:String, address: Int) = this(command, new InetSocketAddress(address))

  override def apply(request:HttpRequest) = {

    if ( ! executor.isRunning) executor.exec()

    port(request)
  }
}

// vim: set ts=2 sw=2 et:
