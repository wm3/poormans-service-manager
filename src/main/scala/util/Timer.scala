package jp.w3ch.psm.util

import com.twitter.util.Duration
import com.twitter.util.Time

class Timer(duration:Duration, fn: => Unit) {
  @volatile var finished = false
  
  val thread = new Thread {
    override def run() {
      while(! finished) {
        fn
        Thread.sleep(duration.inMillis);
      }
      }
  }
  thread.setDaemon(true)
  thread.start
  
  def stop() {
    finished = true
  }
}

object Timer {
  def schedule(duration:Duration)(fn: => Unit):Timer = {
    return new Timer(duration, fn)
  }
}
