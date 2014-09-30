package com.github.tomtung.jsimhash

import java.nio.charset.Charset

import com.google.common.hash.{Funnel, Hashing, HashFunction}

class SimHashBuilder(val hashFunction: HashFunction) extends SimHashBasicBuilder {

  def this() {
    this(Hashing.md5())
  }

  def addBytesFeature(input: Array[Byte]): Unit = {
    this.addBytesFeature(input, 1.0)
  }

  def addBytesFeature(input: Array[Byte], weight: Double): Unit = {
    this.addFeatureHash(hashFunction.hashBytes(input).asLong(), weight)
  }

  def addBytesFeature(input: Array[Byte], off: Int, len: Int): Unit = {
    this.addBytesFeature(input, off, len, 1.0)
  }

  def addBytesFeature(input: Array[Byte], off: Int, len: Int, weight: Double): Unit = {
    this.addFeatureHash(hashFunction.hashBytes(input, off, len).asLong(), weight)
  }

  def addIntFeature(input: Int): Unit = {
    this.addIntFeature(input, 1.0)
  }

  def addIntFeature(input: Int, weight: Double): Unit = {
    this.addFeatureHash(hashFunction.hashInt(input).asLong(), weight)
  }

  def addLongFeature(input: Long): Unit = {
    this.addLongFeature(input, 1.0)
  }

  def addLongFeature(input: Long, weight: Double): Unit = {
    this.addFeatureHash(hashFunction.hashLong(input).asLong(), weight)
  }

  def addStringFeature(input: CharSequence): Unit = {
    this.addStringFeature(input, 1.0)
  }

  def addStringFeature(input: CharSequence, weight: Double): Unit = {
    this.addFeatureHash(hashFunction.hashUnencodedChars(input).asLong(), weight)
  }

  def addStringFeature(input: CharSequence, charset: Charset): Unit = {
    this.addStringFeature(input, charset, 1.0)
  }

  def addStringFeature(input: CharSequence, charset: Charset, weight: Double): Unit = {
    this.addFeatureHash(hashFunction.hashString(input, charset).asLong(), weight)
  }

  def addObjectFeature[T](instance: T, funnel: Funnel[_ >: T]): Unit = {
    this.addObjectFeature(instance, funnel, 1.0)
  }

  def addObjectFeature[T](instance: T, funnel: Funnel[_ >: T], weight: Double): Unit = {
    this.addFeatureHash(hashFunction.hashObject(instance, funnel).asLong(), weight)
  }
}
