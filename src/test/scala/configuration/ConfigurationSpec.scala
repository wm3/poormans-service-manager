package jp.w3ch.psm

import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.Service
import org.specs2.mutable._
import org.specs2.mock._

class ConfigurationSpec extends Specification with Mockito {

  "can register a listener" >> {

    "with a minimum setting" in {
      val service = mock[DispatchingServer.Proxy]

      val config = new Configuration {
        listen = 3000
        default -> service
      }

      config.proxyHandler must_== Seq()
    }

    "with a virtal host" in {
      val service = mock[DispatchingServer.Proxy]

      val config = new Configuration {
        listen = 3000

        when("localhost") -> service
        default           -> textResponse("hello")
      }

      config.proxyHandler must_== Seq(DispatchingServer.HostUrl("localhost") -> service)
    }
  }

}

// vim: set ts=2 sw=2 et: