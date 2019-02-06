package utils

import java.sql.Timestamp

import org.scalatest.{FlatSpec, Matchers}


class StringImprovementsTest extends FlatSpec  with Matchers {

  import utils.StringImplicits._

  "toIntSafe" should "convert string to int" in {

    val res: Option[Int] = "12345".toIntSafe

    res shouldBe Some(12345)

  }

  it should "return None for incorrect Int" in {

    val res = "notInt".toIntSafe

    res shouldBe None
  }

  "toLongSafe" should "convert string to Long" in {

    val res: Option[Long] = "123451231234".toLongSafe

    res shouldBe Some(123451231234L)

  }

  it should "return None for incorrect Long" in {

    val res = "notLong".toLongSafe

    res shouldBe None
  }

  "toTimeStampSafe" should "convert string to timestamp" in {

    val res = "2005-04-06 09:01:10".toTimeStampSafe

    res shouldBe Some(Timestamp.valueOf("2005-04-06 09:01:10.0"))

  }

  it should "return None for incorrect timestamp" in {

    val res = "notTime".toTimeStampSafe

    res shouldBe None
  }

}
