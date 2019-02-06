package helpers

import org.scalatest.{FlatSpec, Matchers}

class MapperTest extends FlatSpec  with Matchers {

  private val correctMap    = "data->0,firstname->4,lastname->5,ip->3,url->7,date->1"
  private val incorrectMap  = "data->0,firstname->4,lastname->5,ip->3,url->7,date1"
  private val noMap         = null
  private val otherMap      = "6->lang,4->source,18->another"


  "buildMap" should "build correct map with string-to-int" in {

    val map: Map[String, Int] = Mapper.buildMap[String, Int](correctMap, Mapper.stringToString, Mapper.stringToInt)

    map should have size 6

    map.contains("data")      shouldBe true
    map.contains("firstname") shouldBe true
    map.contains("lastname")  shouldBe true
    map.contains("ip")        shouldBe true
    map.contains("url")       shouldBe true
    map.contains("date")      shouldBe true

    map.contains("obtain")    shouldBe false
    map.contains("street")    shouldBe false

    map("data")               shouldBe 0
    map("firstname")          shouldBe 4
    map("lastname")           shouldBe 5
    map("ip")                 shouldBe 3
    map("url")                shouldBe 7
    map("date")               shouldBe 1

  }

  it should "build correct map with int-to-string" in {

    val map: Map[Int, String] = Mapper.buildMap[Int, String](otherMap, Mapper.stringToInt, Mapper.stringToString)

    map should have size 3

    map.contains(6)   shouldBe true
    map.contains(4)   shouldBe true
    map.contains(18)  shouldBe true


    map.contains(1)   shouldBe false
    map.contains(5)   shouldBe false

    map(6)            shouldBe "lang"
    map(4)            shouldBe "source"
    map(18)           shouldBe "another"

  }

  it should "build incorrect" in {

    val map: Map[String, Int] = Mapper.buildMap[String, Int](noMap, Mapper.stringToString, Mapper.stringToInt)

    map should not be null
    map should have size 0

  }

  it should "crash on incorrect map build" in {

    an [Exception] should be thrownBy
    Mapper.buildMap[String, Int](incorrectMap, Mapper.stringToString, Mapper.stringToInt)

  }

  "buildMapSS" should "build correct map" in {

    val map      : Map[String, Int] = Mapper.buildMapSS(correctMap)
    val sampleMap: Map[String, Int] = Mapper.buildMap[String, Int](correctMap, Mapper.stringToString, Mapper.stringToInt)

    map shouldBe sampleMap

  }

  "buildMapIS" should "build correct map" in {

    val map      : Map[Int, String] = Mapper.buildMapIS(otherMap)
    val sampleMap: Map[Int, String] = Mapper.buildMap[Int, String](otherMap, Mapper.stringToInt, Mapper.stringToString)

    map shouldBe sampleMap

  }


}
