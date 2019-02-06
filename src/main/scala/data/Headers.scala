package data

object Headers {

  private val headers = Map(
    "data,time stamp,country,ipaddress,firstname,lastname,lang,source"
      -> Map(
        "columns" -> "data->0,firstname->4,lastname->5,ip->3,url->7,date->1",
        "others"  -> "6->lang"
      ),
    "data,date_joined,country,ipaddress,firstname,lastname,lang,source"
      -> Map(
        "columns" -> "data->0,firstname->4,lastname->5,ip->3,url->7,date->1",
        "others"  -> "6->lang"
      ),
    "data,firstname,lastname,ip,source,inserted"
      -> Map(
        "columns" -> "data->0,firstname->1,lastname->2,ip->3,url->4,date->5",
        "others"  -> null
      )
  )

  def get: Map[String, Map[String, String]] = headers

}
