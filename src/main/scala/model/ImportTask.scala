package model

case class ImportTask(
    jobID: Int,
    timeStamp: Long,
    owner: String,
    link: String,
    origin: ImportOrigin,
    remap: Option[ReMapRules]
)

case class ImportOrigin(
    provider: String,
    profile: String,
    country: String,
    date: Long,
    source: String
)

case class ReMapRules(
    columns: String,
    other: String
)
