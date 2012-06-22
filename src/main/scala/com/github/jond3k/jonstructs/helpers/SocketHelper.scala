package com.github.jond3k.jonstructs.helpers

import java.net.{BindException, ServerSocket}

/**
 * Adds behaviour that allows you to quickly allocate and deallocate ports in tests
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait SocketHelper {

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
   * Like findFreePort() but returns a list of ports
   */
  def findFreePorts(count: Int): List[Int] = {
    val sockets =
      for (i <- 0 until count)
      yield new ServerSocket(0)
    val socketList = sockets.toList
    val ports = socketList.map(_.getLocalPort.ensuring(_ > 0, "port <= 0"))
    socketList.map(_.close())
    ports
  }

  /**
   * Allows you to pick a free port.
   *
   * We create a socket with an OS-determined port. We then close the socket and use the port for our own purposes. For
   * this reason there's a rare potential race condition. To make this a less likely to happen, make sure repeated calls
   * are sequential.
   *
   */
  def findFreePort(): Int = findFreePorts(1).head.ensuring(_ > 0, "port <= 0")
}
