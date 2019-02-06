package helpers

object Mapper {

  def buildMapSS(s: String): Map[String, Int] =
    buildMap[String, Int](s, Mapper.stringToString, Mapper.stringToInt)

  def buildMapIS(s: String): Map[Int, String] =
    buildMap[Int, String](s, Mapper.stringToInt, Mapper.stringToString)

  def buildMap[A, B](s: String, typeA: String => A, typeB: String => B): Map[A, B] = {

    s match {
      case s: String =>
        s.toLowerCase()
          .split(",")
          .map(v => {
            val a = v.split("->")
            typeA(a(0)) -> typeB(a(1))
          })
          .toMap
      case null =>
        Map.empty[A, B]
    }
  }

  val stringToString: String => String = (s: String) => s
  val stringToInt: String => Int       = (s: String) => s.toInt

}
