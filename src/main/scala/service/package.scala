package jp.w3ch.psm

import com.twitter.finagle.Service
import org.jboss.netty.handler.codec.http._


package object service {

  type HttpService = Service[HttpRequest, HttpResponse]

}

// vim: set ts=2 sw=2 et:
