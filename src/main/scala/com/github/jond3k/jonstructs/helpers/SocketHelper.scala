package com.github.jond3k.jonstructs.helpers

import java.net.{Socket, BindException, ServerSocket}
import com.github.jond3k.jonstructs.blocks.RetryBlock
import annotation.tailrec

/**
 * Adds behaviour that allows you to quickly allocate and deallocate ports in tests
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait SocketHelper extends RetryBlock {

  /**
   * Determine if a port is free for us to use
   *
   * FIXME: It will only return false if the error message is an "in use" error. For that reason, this code is probably
   * not portable as there's no standard set of error messages. Consider just trying to connect as a client in future.
   *
   * @param port The port number
   * @return Free or not?
   */
  def isPortFree(port: Int) = {
    try {
      val ss = new ServerSocket(port)
      ss.close()
      true
    } catch {
      case e: BindException if (e.getMessage == "Address already in use") => false
    }
  }

  /**
   * Allows you to pick a free port. Will allocate anything over 1024
   *
   * We create a socket with an OS-determined port. We then close the socket and use the port for our own purposes. For
   * this reason there's a rare potential race condition. To make this a less likely to happen, make sure repeated calls
   * are sequential.
   */
  def findFreePort(): Int = {
    val socket = new ServerSocket(0)
    val port   = socket.getLocalPort.ensuring(_ > 0, "port <= 0")
    socket.close()
    port
  }

  /**
   * Will sweep from that value upwards until it finds a free port, starting from a specified value
   *
   * This makes sense if you want a 'best case' port or want the port to have a more obvious value. I created this
   * mostly because it's difficult to add new ZooKeeper hosts to the Eclipse ZooKeeper plugin, so having it reuse common
   * values like 22181 means I can reuse the entry between integration test runs.
   *
   * @param startPort The port to begin scanning from
   * @return The port
   */
  @tailrec
  final def findFreePort(startPort: Int): Int = {
    isPortFree(startPort) match {
      case true  => startPort
      case false => findFreePort(startPort+1)
    }
  }

  /**
   * Wait for the specified port to be opened
   *
   * @param port The port number
   */
  def waitForServer(port: Int) {
    waitForServer("localhost", port)
  }

  def waitForServer(host: String, port: Int) {
    val timeoutMs = 2000
    val ms        = 250
    val times     = java.lang.Math.floor(timeoutMs/ms).toLong

    retry(ms, times) {
      val sock = new Socket(host, port)
      sock.close()
    }
  }
}
