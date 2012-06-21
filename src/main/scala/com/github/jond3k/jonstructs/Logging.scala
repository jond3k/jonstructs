package com.github.jond3k.jonstructs

import org.slf4j.LoggerFactory

/**
 * Mix this trait in to have access to a logger. Identical interface to dropwizard-scala's Logging trait except it
 * doesn't use the problematic dropwizard logger abstraction layer
 */
trait Logging {
  protected lazy val log = LoggerFactory.getLogger(getClass)
}