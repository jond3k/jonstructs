package com.github.jond3k.jonstructs.helpers

import java.net.ServerSocket

/**
 * Licenses are for teh weak
 * ~ So sayeth the wise dalmaster
 */

/**
 * Allows you to pick a free port.
 *
 * We create a socket with an OS-determined port. We then close the socket and use the port for our own purposes. For
 * this reason there's a rare potential race condition. To make this a less likely to happen, make sure repeated calls
 * are sequential.
 */
trait FreePortHelper {

  /**
   * Choose a number of random available ports
   */
  def findFreePort(count: Int): List[Int] = {
    val sockets =
      for (i <- 0 until count)
      yield new ServerSocket(0)
    val socketList = sockets.toList
    val ports = socketList.map(_.getLocalPort.ensuring(_ > 0, "port <= 0"))
    socketList.map(_.close())
    ports
  }

  /**
   * Choose an available port.
   */
  def findFreePort(): Int = findFreePort(1).head.ensuring(_ > 0, "port <= 0")
}
