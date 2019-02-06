package helpers

import model.{DSHeader, MetaData}
import org.apache.spark.sql.Row
import utils.Hasher

object Converter extends Hasher {

  def convert(cmap: Map[String, Int], omap: Map[Int, String])(md: MetaData, row: Row): Tuple1[DSHeader] = {

    def get(colm: String): String = getVal(row, cmap, colm)

    val data = get("data")

    Tuple1(
      Option(data).map { d =>
        val split = dataSpliter(d)

        val dataA = split(0)
        val dataB = {
          if (split.isDefinedAt(1))
            split(1)
          else
            ""
        }

        DSHeader(
          country     = md.country,
          provider    = md.provider,
          profile     = md.profile,
          date_import = md.date_import,
          dt_create   = md.dt_create,
          data        = d,
          dataA       = dataA,
          dataB       = dataB,
          hash        = md5(dataA),
          firstname   = get("firstname"),
          lastname    = get("lastname"),
          ip          = get("ip"),
          url         = get("url"),
          date        = get("date"),
          gender      = get("gender"),
          address1    = get("address1"),
          address2    = get("address2"),
          zip         = get("zip"),
          city        = get("city"),
          others      = mkOthers(row, omap)
        )
      }.orNull
    )

  }

  def getVal(input: Row, m: Map[String, Int], colm: String): String = {
    if (m.contains(colm))
      input.getString(m(colm))
    else
      null
  }

  def mkOthers(input: Row, m: Map[Int, String]): String = {
    if (m.nonEmpty)
      m.map { case (k, v) => v + "=" + input.get(k) }.mkString("#")
    else
      null
  }

  def dataSpliter(data: String) =
    data.toLowerCase.split("-")


}
