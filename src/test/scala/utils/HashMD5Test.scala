package utils

import org.scalatest.{FlatSpec, Matchers}

class HashMD5Test extends FlatSpec with Matchers {

  val hasher = new Hasher { }

  val sampleA   = "some-long-data-sample"
  val hashA     = "b077e03e861e98c106a0d56b7c49cf54"
  val sampleB   = "another-data-sample"

  "md5" should "return correct hash" in {

    val hash = hasher.md5(sampleA)

    hash shouldBe hashA

  }

  it should "return always a same value" in {
    val hash1 = hasher.md5(sampleB)
    val hash2 = hasher.md5(sampleB)
    val hash3 = hasher.md5(sampleA)

    hash1 shouldBe hash2
    hash1 should not be  hash3
  }

}
