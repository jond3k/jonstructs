package com.github.jond3k.jonstructs.blocks

/**
 * This class isn't an executable specification, it just ensures the DSL syntax can be parsed by the compiler
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
class RunSyntax extends RunBlocks {

  delayed 3 seconds {
    // code that gets run later
  }

  every 5 seconds {
    // code that gets called regularly
  }

  given 3 seconds {
    // code that has a limited time to execute*
  }

  retry 3 times every 5 seconds {
    // code that is repeatedly run at regular intervals
  }


}
