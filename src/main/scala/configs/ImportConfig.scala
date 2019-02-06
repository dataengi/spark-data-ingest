package configs

trait ImportConfig {
  this: HasConfig =>

  lazy val appProcessName      = config.getString("app.process.name")
  lazy val appServiceName      = config.getString("app.service.name")

  lazy val mergeTableName      = config.getString("hive.dataTable")
  lazy val statisticsTableName = config.getString("hive.statisticsTable")

  lazy val delimiter           = config.getString("process.filename.delimiter")

}
