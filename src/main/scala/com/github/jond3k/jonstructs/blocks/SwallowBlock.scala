package com.github.jond3k.jonstructs.blocks

import java.io.{PrintWriter, StringWriter}

trait SwallowBlock {

  /**
   * Generate a log entry for an exception we wish to follow
   * slf4j and other logging frameworks support this out the
   * box, but having our own allows support for jvm logging
   * and println
   *
   * @param t The exception
   * @return The message
   */
  protected def _swallowString(t: Throwable) = {
    val result      = new StringWriter
    val printWriter = new PrintWriter(result)
    t.printStackTrace(printWriter)
    "Swallowed %s: %s\n%s" format (t.getClass.getSimpleName, t.getMessage, result.toString)
  }

  /**
   * Swallow an exception, logging it with the specified log function. There
   * may be a return value if it was successful.
   *
   * @param h  The logger function
   * @param a  The method to call
   * @tparam A The return type
   * @return The return value on success
   */
  def swallow[A](h: (String) => Unit = null)(a: => A): Option[A] = {
    try {
      Some(a)
    } catch {
      case t: Throwable => {
        Option(h) match {
          case Some(hh) => hh(_swallowString(t))
          case None     =>
        }
        Option.empty[A]
      }
    }
  }

}
