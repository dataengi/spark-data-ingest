package model

case class ImportStatistics(
    provider_id: String,
    profile_id: String,
    name_file_in: String,
    dt_create: Long,
    date_import: Long,
    total_cnt: Long,
    valid_cnt: Long,
    unique_cnt: Long
)
