package formatters

import model._
import play.api.libs.json.{Json, OFormat, _}

object ImportTaskFormat {

  implicit val importOriginRW: OFormat[ImportOrigin]         = Json.format[ImportOrigin]
  implicit val remapRulesRW: OFormat[ReMapRules]             = Json.format[ReMapRules]
  implicit val importTaskRW: OFormat[ImportTask]             = Json.format[ImportTask]
  implicit val importStatisticsRW: OFormat[ImportStatistics] = Json.format[ImportStatistics]
  implicit val taskIdReads: Reads[TaskID]                    = Json.format[TaskID]

}
