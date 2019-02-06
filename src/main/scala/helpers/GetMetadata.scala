package helpers

import model.{FileInfo, ImportOrigin, MetaData}

object GetMetadata {

  import utils.StringImplicits._

  def fromFile(file: FileInfo) =
    MetaData(
      country     = file.country,
      provider    = file.dataprovider,
      profile     = file.dataprofile,
      date_import = file.date.toLongSafe.getOrElse(0),
      dt_create   = System.currentTimeMillis
    )

  def fromJson(origin: ImportOrigin) =
    MetaData(
      country     = origin.country,
      provider    = origin.provider,
      profile     = origin.profile,
      date_import = origin.date,
      dt_create   = System.currentTimeMillis
    )

}
