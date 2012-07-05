package com.github.jond3k.jonstructs.blocks

trait SwallowBlock {

  /**
   * Swallow an exception. There may be a return value if it was successful
   *
   * @param  a The method to call
   * @tparam A The return type
   * @return The return value on success
   */
  def swallow[A](a: => A): Option[A] = {
    swallow(null, a)
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
  def swallow[A](h: (String, Exception) => Unit, a: => A): Option[A] = {
    try {
      Some(a)
    } catch {
      case t: Throwable => {
        Option(h) match {
          case Some(i) => i("Swallowed exception", t)
          case None    =>
        }
        Option.empty[A]
      }
    }
  }
}
