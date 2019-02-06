package helpers

import model.{DSHeader, MetaData}
import org.apache.spark.sql.Row
import org.scalatest.{FlatSpec, Matchers}

class ConverterTest extends FlatSpec  with Matchers {


  private val goodSample    = Row.fromSeq(Seq("dataA-dataB-dataC", "John", "Dao", "LA", "14:30", "127.0.0.1", "www.someurl.com", "01/02/03", "M", "Hollywood", "LA", "1234567", "Los Angeles"))
  private val lowerSample   = Row.fromSeq(Seq("dataA-dataB-dataC-dataD", "John", "Dao", "LA", "14:30", "127.0.0.1", "www.someurl.com", "01/02/03", "M", "Hollywood", "LA", "1234567", "Los Angeles"))
  private val brokenSample  = Row.fromSeq(Seq(null, "John", "Dao", "LA", "14:30", "127.0.0.1", "www.someurl.com", "01/02/03", "M", "Hollywood", "LA", "1234567", "Los Angeles"))
  private val colmnMapLong  = Map ("data"->0,"firstname"->1,"lastname"->2,"ip"->5,"url"->6,"date"->7, "gender"->8, "address1"->9, "address2"->10, "zip"->11, "city"->12)
  private val colmnMapShort = Map ("data"->0,"firstname"->1,"lastname"->2)
  private val wOtherMap     = Map (3->"region", 4->"time")
  private val noOtherMap    = Map [Int, String]()
  private val metadata      = MetaData ("UA", "providerName", "UA_files", 1530011462, 1234)


  "getValue" should "return correct values from Row" in {

    def get(colm: String): String = Converter.getVal(goodSample, colmnMapShort, colm)

    get("data") shouldBe "dataA-dataB-dataC"
    get("firstname") shouldBe "John"
    get("lastname") shouldBe "Dao"
    get("region") shouldBe null
    get("time") shouldBe null
  }

  "mkOther" should "build others string" in {

    val others = Converter.mkOthers(goodSample, wOtherMap)

    others should not be null

    others shouldBe "region=LA#time=14:30"

  }

  it should "build empty others" in {

    val others = Converter.mkOthers(goodSample, noOtherMap)

    others shouldBe null

  }

  "convert" should "return correct DSHeader with all attributes good sample" in {

    val tuple: Tuple1[DSHeader] = Converter.convert(colmnMapLong, wOtherMap)(metadata, goodSample)

    tuple    should not be null
    tuple._1 should not be null

    val row = tuple._1
    row.country        shouldBe  metadata.country
    row.provider       shouldBe  metadata.provider
    row.profile        shouldBe  metadata.profile
    row.date_import    shouldBe  metadata.date_import
    row.dt_create      shouldBe  metadata.dt_create
    row.data           shouldBe  "dataA-dataB-dataC"
    row.dataA          shouldBe  "dataa"
    row.dataB          shouldBe  "datab"
    row.hash           shouldBe  "38af2bc7b0a4b94e48722c7fbd601ad4"
    row.firstname      shouldBe  "John"
    row.lastname       shouldBe  "Dao"
    row.ip             shouldBe  "127.0.0.1"
    row.url            shouldBe  "www.someurl.com"
    row.date           shouldBe  "01/02/03"
    row.gender         shouldBe  "M"
    row.address1       shouldBe  "Hollywood"
    row.address2       shouldBe  "LA"
    row.zip            shouldBe  "1234567"
    row.city           shouldBe  "Los Angeles"
    row.others         shouldBe  "region=LA#time=14:30"

  }

  it should "return correct DSHeader with some attributes good sample" in {

      val tuple: Tuple1[DSHeader] = Converter.convert(colmnMapShort, noOtherMap)(metadata, goodSample)

      tuple    should not be null
      tuple._1 should not be null

      val row = tuple._1
      row.country        shouldBe  metadata.country
      row.provider       shouldBe  metadata.provider
      row.profile        shouldBe  metadata.profile
      row.date_import    shouldBe  metadata.date_import
      row.dt_create      shouldBe  metadata.dt_create
      row.data           shouldBe  "dataA-dataB-dataC"
      row.dataA          shouldBe  "dataa"
      row.dataB          shouldBe  "datab"
      row.hash           shouldBe  "38af2bc7b0a4b94e48722c7fbd601ad4"
      row.firstname      shouldBe  "John"
      row.lastname       shouldBe  "Dao"
      row.ip             shouldBe  null
      row.url            shouldBe  null
      row.date           shouldBe  null
      row.gender         shouldBe  null
      row.address1       shouldBe  null
      row.address2       shouldBe  null
      row.zip            shouldBe  null
      row.city           shouldBe  null
      row.others         shouldBe  null

  }

  it should "return correct DSHeader with expected data parts" in {

    val tuple: Tuple1[DSHeader] = Converter.convert(colmnMapShort, noOtherMap)(metadata, lowerSample)

    tuple    should not be null
    tuple._1 should not be null

    val row = tuple._1
    row.data      shouldBe  "dataA-dataB-dataC-dataD"
    row.dataA     shouldBe  "dataa"
    row.dataB     shouldBe  "datab"
    row.hash      shouldBe  "38af2bc7b0a4b94e48722c7fbd601ad4"

  }

  it should "return None for broken sample" in {
    val tuple: Tuple1[DSHeader] = Converter.convert(colmnMapShort, noOtherMap)(metadata, brokenSample)

    tuple    should not be null
    tuple._1 shouldBe null
  }


}
