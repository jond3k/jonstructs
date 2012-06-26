package com.github.jond3k.jonstructs.helpers

import java.io.File
import java.util

/**
 * Licenses are for teh weak
 * ~ So sayeth the wise dalmaster
 */

/**
 * Allows creation of randomly named directories. Useful when you need to provide a folder to mocks and fakes.
 */
trait DirectoryHelper {

  /**
   * Creates a new temporary directory
   *
   * Note that while the folders will normally be deleted on shutdown, if the program terminates prematurely (e.g.
   * unhandled exception when running tests) then the folder will be left behind. This does make it easier to debug
   * the cause of the failure, however.
   *
   * @param prefix The prefix to use
   * @return A path to a new test folder. Go forth and multiply!
   */
  def newTempDir(prefix: String = "jonstructs"): File = {
    val ioDir = System.getProperty("java.io.tmpdir")
    val f = new File(ioDir, prefix + jonstructsRandom.nextInt(1000000))
    f.mkdirs()
    f.deleteOnExit()
    f
  }

  /**
   * The random number generator.
   *
   * There's no guarantee this is thread safe across different JVMs, but looks safe in OpenJDK
   */
  private lazy val jonstructsRandom = new util.Random()
}