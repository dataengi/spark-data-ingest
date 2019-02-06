package com

import configs.{ImportConfig, HasConfig}
import data._
import helpers.{Converter, GetMetadata, InDirFiles, Mapper}
import model.{FileInfo, ImportStatistics}
import org.apache.hadoop.fs.{FileStatus, FileSystem}
import org.apache.spark.sql._
import utils.spark.SparkActions


object ImportBatch extends SparkActions with HasConfig with ImportConfig {

  lazy val spark: SparkSession = InitSpark(appProcessName)

  def main(args: Array[String]): Unit = {

    if (args.length < 1) {
      <<("required parameter: {directory} ")
      System.exit(1)
    }

    val directory = args(0)

    val files     = getFiles(directory)

    val metadata  = extractMetadata(files)

    val processed = processMetadata(metadata)

    showSummary(files, processed)

  }

  def getFiles(directory: String): Array[FileStatus] = {
    val filesystem  = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    InDirFiles.getFilesList(filesystem, directory)
  }

  def extractMetadata(files: Array[FileStatus]): Array[FileInfo] =
    files
      .flatMap(InDirFiles.extractMetadataFromFilename(_))


  def processMetadata(metadata: Array[FileInfo]): Int =
    metadata
      .map(processFile)
      .count(identity)


  def processFile(file: FileInfo): Boolean = {

    <<(file toString())

    val inDF = spark.read
      .format("csv")
      .option("header", "true")
      .load(file.filepath)


    // check header
    val csvcolumns = inDF.columns.mkString(",")
    if (!Headers.get.contains(csvcolumns)) {

      <<(s"UNKNOWN HEADER!\n${csvcolumns}")

      false

    } else {

      <<(s"Found header\n${csvcolumns} ")


      val meta = GetMetadata.fromFile(file)

      // prepare conversion recipe
      val headermap = Headers.get(csvcolumns)
      val cmap = Mapper.buildMapSS(headermap("columns"))
      val omap = Mapper.buildMapIS(headermap("others"))
      val remap = Converter.convert(cmap, omap)(_, _)

      def skipNulls (v: Tuple1[_]) =  v._1 != null

      // remap data
      import spark.implicits._
      val processed = inDF.map(remap(meta, _)).filter(skipNulls _).map(_._1)
      val result    = processed.cache()

      <<(s"Save to table $mergeTableName")
      insertIntoHive(result.toDF(), mergeTableName)


      // rows stats
      val total     = inDF.count()
      val valid     = result.count()
      val diff      = total - valid
      val uniq      = result.distinct().count()


      <<(s"total: $total\nvalid: $valid\ndiff: $diff\ndistinct: $uniq")
      val statistics = ImportStatistics (
        provider_id   = meta.provider,
        profile_id    = meta.profile,
        name_file_in  = file.filename,
        dt_create     = meta.dt_create,
        date_import   = meta.date_import,
        total_cnt     = total,
        valid_cnt     = valid,
        unique_cnt    = uniq
      )

      insertIntoHive(Seq(statistics).toDF(), statisticsTableName)

      true
    }
  }

  def showSummary(files: Array[FileStatus], processed: Int): Unit = {

    <<(files.map(_.toString).mkString("\n"))
    <<(s"total: ${files.length}\nprocessed: ${processed}")
  }

}

