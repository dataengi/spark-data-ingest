package json

import model.{ImportOrigin, ImportTask, ReMapRules}
import org.scalatest.{FlatSpec, Inside, Matchers}
import play.api.libs.json.{JsSuccess, Json}
import formatters.ImportTaskFormat._

class ImportTaskParserTest extends FlatSpec with Matchers with Inside {


  val sampleValidOrigin = {
    """
      |{
      |    	"provider": "ProviderName",
      |    	"profile": "Files_UA",
      |    	"country":	"UA",
      |    	"date":	12345678,
      |    	"source":	"/origin-file-path/file-name.csv"
      |}
    """.stripMargin
  }

  val sampleResultOrigin = ImportOrigin ("ProviderName", "Files_UA", "UA", 12345678, "/origin-file-path/file-name.csv")

  val sampleValidRemap = {
    """
      |{
      |    	"columns": "data->0,date->1",
      |    	"other": "2->lang"
      |}
    """.stripMargin
  }

  val sampleResultReMap =  ReMapRules ("data->0,date->1", "2->lang")

  val sampleValidTask =
    """
      |{
      |	   "jobID": 1,
      |    "timeStamp": 1234567890,
      |    "owner": "User ID",
      |    "link": "s3://somefolder/input.csv",
      |    "origin": {
      |    	"provider": "ProviderName",
      |    	"profile": "Files_UA",
      |    	"country":	"UA",
      |    	"date":	12345678,
      |    	"source":	"/origin-file-path/file-name.csv"
      |    },
      |    "remap":{
      |    	"columns":"data->0,date->1",
      |    	"other":"2->lang"
      |    }
      |}
    """.stripMargin

  val sampleResultTask = ImportTask(1, 1234567890, "User ID", "s3://somefolder/input.csv", sampleResultOrigin, Some(sampleResultReMap))

  val sampleInCompleteTask =
    """
      |{
      |	   "jobID": 1,
      |    "timeStamp": 1234567890,
      |    "owner": "User ID",
      |    "link": "s3://somefolder/input.csv"
      |}
    """.stripMargin


  "ImportOriginParser" should "parse valid json" in {

    val parsed    = Json.parse(sampleValidOrigin)
    val validated = parsed.validate[ImportOrigin]

    validated.isSuccess shouldBe true


  }

  "remapParser" should "parse valid json" in {

    val parsed    = Json.parse(sampleValidRemap)
    val validated = parsed.validate[ReMapRules]

    validated.isSuccess shouldBe true

    inside(validated) {
      case JsSuccess(result, _) =>
        result shouldBe sampleResultReMap
    }

  }

  "ImportTaskParser" should "parse valid json" in {

    val parsed    = Json.parse(sampleValidTask)
    val validated = parsed.validate[ImportTask]

    validated.isSuccess shouldBe true

    inside(validated) {
      case JsSuccess(result, _) =>
        result shouldBe sampleResultTask
    }

  }

  it should "return error on json without origins" in {

    val parsed    = Json.parse(sampleInCompleteTask)
    val validated = parsed.validate[ImportTask]

    validated.isSuccess shouldBe false

  }

}
