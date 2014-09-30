package com.github.tomtung.jsimhash

object Util {
  def hammingDistance(left: Long, right: Long) = java.lang.Long.bitCount(left ^ right)

  def simHashToString(simHash: Long) = String.format("%64s", java.lang.Long.toBinaryString(simHash)).replace(" ", "0")
}
