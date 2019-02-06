package utils.spark

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

trait SparkActions {

  def InitSpark(appName: String) =
    SparkSession
      .builder()
      .appName(appName)
      .enableHiveSupport()
      .getOrCreate()

  def <<(msg: String) = {
    println("\n============================================")
    println(msg)
    println("============================================\n")
  }

  def readCSV(ss: SparkSession, filename: String, header: String = "false") =
    ss.read
      .format("csv")
      .option("header", header)
      .load(filename)

  def saveToHive(df: DataFrame, tablename: String, mode: SaveMode = SaveMode.Overwrite, format: String = "orc") =
    df.write
      .mode(mode)
      .format(format)
      .saveAsTable(tablename)

  def insertIntoHive(df: DataFrame, tablename: String) =
    df.write
      .insertInto(tablename)

  def saveToCSV(df: DataFrame, filename: String) =
    df.coalesce(1)
      .write
      .format("com.databricks.spark.csv")
      .save(filename)

}
