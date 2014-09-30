package com.github.tomtung.jsimhash

import com.google.common.collect.ArrayListMultimap

import scala.annotation.tailrec
import scala.collection.JavaConversions._

/**
 * A simple SimHash index for finding near-duplicates.
 * For reference, see:
 *    Manku, G. S., Jain, A., & Das Sarma, A. (2007). Detecting Near-duplicates for Web Crawling.
 * For simplicity, we use the second parameter setting in EXAMPLE 3.1
 * @param k SimHash values with Hamming distance less or equal than k are considered near duplicates
 */
class SimHashIndex(val k: Int) {

  assert(k < 32)

  def this() {
    this(3)
  }

  private val buckets: Array[(Long, ArrayListMultimap[Long, Long])] = {
    val result = new Array[(Long, ArrayListMultimap[Long, Long])](k + 1)

    val blockSize = 64 / (k + 1)
    val nBiggerBlocks = 64 % (k + 1)
    @tailrec
    def doInitialize(i: Int = 0, offset: Int = 0): Unit = {
      if (i < k + 1) {
        val currBlockSize =
          if (i < nBiggerBlocks) {
            blockSize + 1
          } else {
            blockSize
          }

        val mask = ((1L << currBlockSize) - 1) << offset
        val multiMap = ArrayListMultimap.create[Long, Long]()
        result(i) = (mask, multiMap)

        doInitialize(i + 1, offset + currBlockSize)
      }
    }

    doInitialize()
    result
  }

  def add(simHash: Long): Unit = {
    for (
      (mask, map) <- this.buckets
    ) {
      map.put(simHash & mask, simHash)
    }
  }

  def remove(simHash: Long): Unit = {
    for (
      (mask, map) <- this.buckets
    ) {
      map.remove(simHash & mask, simHash)
    }
  }

  def hasDuplicate(simHash: Long): Boolean = {
    for (
      (mask, map) <- this.buckets.iterator;
      candidate <- map.get(simHash & mask).iterator()
      if Util.hammingDistance(simHash, candidate) <= k
    ) {
      return true
    }

    false
  }

  def findNearDuplicates(simHash: Long): Array[Long] = {
    (for (
      (mask, map) <- this.buckets.iterator;
      candidate <- map.get(simHash & mask).iterator()
      if Util.hammingDistance(simHash, candidate) <= k
    ) yield {
      candidate
    }).toSet.toArray
  }
}
