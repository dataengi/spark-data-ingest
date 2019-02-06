package com

import configs.{ImportConfig, KafkaConfig, HasConfig}
import data.Headers
import formatters.ImportTaskFormat._
import helpers.{Converter, GetMetadata, Mapper}
import model.{ImportStatistics, ImportTask, TaskID}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import play.api.libs.json._
import utils.errors.oError
import utils.errors.oError.{ErrorDescription, oError, _}
import utils.exceptions.NoColumnMappingException
import utils.kafka.KafkaSink
import utils.spark.SparkActions

import scala.util.Try

object ImportStreaming extends SparkActions with HasConfig with ImportConfig with KafkaConfig {

  lazy val spark: SparkSession   = InitSpark(appServiceName)
  lazy val ssc: StreamingContext = new StreamingContext(spark.sparkContext, Seconds(secs))

  def main(args: Array[String]): Unit = {

    <<(s"""
        |Running $appServiceName
        |kafka: host = $host, port = $port
        |\ttopicIn = $topicIn, topicOut = $topicOut
      """.stripMargin)

    runApp()
  }

  def runApp(): Unit = {

    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](Array(topicIn), kafkaParams)
    )

    val kafkaSink = ssc.sparkContext.broadcast(KafkaSink(kafkaParams))

    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

      <<(s"RDD = $rdd")
      rdd.map(_.value()).collect().foreach { value =>
        <<(s"kafka in: $value")

        val taskID = idFromJson(value)
        val result = for {
          task <- taskFromJson(value).right
          res  <- processTask(task).right
        } yield res

        val answer = result.toJson(taskID)

        kafkaSink.value.send(topicOut, s"ECHO:  $answer")
      }

      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }

    ssc.start()
    ssc.awaitTermination()

  }

  def idFromJson(json: String): TaskID = {

    Try {
      val parsed    = Json.parse(json)
      val validated = parsed.validate[TaskID]
      validated match {
        case JsSuccess(taskID, _) => taskID
        case err @ JsError(_)     => TaskID.none
      }
    }.getOrElse(TaskID.none)
  }

  def taskFromJson(json: String): oError[ImportTask] = {

    Try {

      val parsed    = Json.parse(json)
      val validated = parsed.validate[ImportTask]

      validated match {

        case JsSuccess(task, _) =>
          <<(s"Task config: $json")
          oError(task)

        case err @ JsError(_) =>
          <<(s"ERROR: $err")
          oError(ErrorDescription(err.toString))
      }

    }.getOrElse(oError(ErrorDescription("Cant parse Json!")))

  }

  def processTask(task: ImportTask): oError[JsValue] = {
    handleException {
      <<(s"""
           |Working on import task: ${task.jobID}
           |set by: ${task.owner} at: ${task.timeStamp}
           |source link: ${task.link}
      """.stripMargin)

      doProcessing(task)
    }
  }

  def doProcessing(task: ImportTask): JsValue = {

    val meta = GetMetadata.fromJson(task.origin)

    val filename = task.link
    val inDF = spark.read
      .format("csv")
      .option("header", "true")
      .load(filename)

    val embeddedMapping = task.remap.map(
      v => (Mapper.buildMapSS(v.columns), Mapper.buildMapIS(v.other))
    )

    val mapping: Option[(Map[String, Int], Map[Int, String])] =
      if (embeddedMapping.isDefined) {

        <<(s"Using task predefined column mapping")
        embeddedMapping

      } else {

        val csvcolumns = inDF.columns.mkString(",")

        if (Headers.get.contains(csvcolumns)) {
          <<(s"Using column mapping for header\n${csvcolumns} ")

          val headermap = Headers.get(csvcolumns)

          Some((Mapper.buildMapSS(headermap("columns")), Mapper.buildMapIS(headermap("others"))))
        } else {

          <<(s"UNKNOWN HEADER!\n${csvcolumns}")
          throw new NoColumnMappingException(s"Column mapping not found for: $csvcolumns")

        }
      }

    mapping match {
      case Some(columns) => {
        import spark.implicits._

        val remap = Converter.convert(columns._1, columns._2)(_, _)

        def skipNulls(v: Tuple1[_]) = v._1 != null

        val processed = inDF.map(remap(meta, _)).filter(skipNulls _).map(_._1)
        val result    = processed.cache()

        <<(s"Save to table $mergeTableName")
        insertIntoHive(result.toDF(), mergeTableName)

        // rows stats
        val total = inDF.count()
        val valid = result.count()
        val diff  = total - valid
        val uniq  = result.distinct().count()

        result.unpersist()

        <<(s"total: $total\nvalid: $valid\ndiff: $diff\ndistinct: $uniq")
        val statistics = ImportStatistics(
          provider_id = meta.provider,
          profile_id = meta.profile,
          name_file_in = filename,
          dt_create = meta.dt_create,
          date_import = meta.date_import,
          total_cnt = total,
          valid_cnt = valid,
          unique_cnt = uniq
        )

        insertIntoHive(Seq(statistics).toDF(), statisticsTableName)

        Json.toJson(statistics)
      }
    }
  }

}
