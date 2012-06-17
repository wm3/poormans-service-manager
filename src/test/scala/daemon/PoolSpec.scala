package jp.w3ch.psm.daemon

import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.Service
import org.specs2.mutable._
import org.specs2.mock._

import jp.w3ch.psm.DispatchingServer
import jp.w3ch.psm.service.HttpService


class PoolSpec extends Specification with Mockito {

  "stops all daemons" >> {

    "when single daemon is running" in {
      val daemon = mock[Daemon]

      val subject = new Pool(Seq(daemon))

      subject.stop()

      there was one(daemon).stop()
    }

    "when multiple daemons are running" in {
      val daemons = Seq(mock[Daemon], mock[Daemon])

      val subject = new Pool(daemons)

      subject.stop()

      there was one(daemons(0)).stop()
      there was one(daemons(1)).stop()
    }
  }

}

// vim: set ts=2 sw=2 et:
