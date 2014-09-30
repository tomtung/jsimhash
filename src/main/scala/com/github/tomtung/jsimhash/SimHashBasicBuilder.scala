package com.github.tomtung.jsimhash

import scala.annotation.tailrec

/**
 * A basic builder for building 64-bit SimHash from feature hash values.
 * For details, see:
 *    Charikar, M. S. (2002). Similarity Estimation Techniques from Rounding Algorithms.
 */
class SimHashBasicBuilder {
  private val counter = new Array[Double](64)

  def reset(): Unit = {
    java.util.Arrays.fill(this.counter, 0.0)
  }

  def computeResult(): Long = {
    @tailrec
    def doCompute(i: Int = 0, hash: Long = 0): Long = {
      if (i < 64) {
        val currBit =
          if (counter(i) > 0) {
            1
          } else {
            0
          }

        doCompute(i + 1, hash | (currBit << i))
      } else {
        hash
      }
    }

    doCompute()
  }

  def addFeatureHash(hash: Long, weight: Double = 1.0): Unit = {
    @tailrec
    def doAdd(i: Int = 0, shiftedHash: Long = hash): Unit = {
      if (i < 64) {
        if ((shiftedHash & 1) == 1) {
          counter(i) += weight
        } else {
          counter(i) -= weight
        }
        doAdd(i + 1, shiftedHash >> 1)
      }
    }

    doAdd()
  }
}
