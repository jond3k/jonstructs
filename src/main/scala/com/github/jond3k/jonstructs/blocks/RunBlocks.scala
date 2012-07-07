package com.github.jond3k.jonstructs.blocks

/**
 * Mixes in all the run blocks
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait RunBlocks
  extends TimeoutBlock
  with RetryBlock
  with DelayedBlock
  with EveryBlock
  with SwallowBlock {

}
