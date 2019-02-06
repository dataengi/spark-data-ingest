package utils

import java.sql.Timestamp

object StringImplicits {

  implicit class StringImprovements (val s: String) {
    import scala.util.control.Exception.catching

    def toIntSafe: Option[Int]             = catching(classOf[NumberFormatException]) opt s.toInt
    def toLongSafe: Option[Long]           = catching(classOf[NumberFormatException]) opt s.toLong
    def toTimeStampSafe: Option[Timestamp] = catching(classOf[IllegalArgumentException]) opt Timestamp.valueOf(s)
  }
}
