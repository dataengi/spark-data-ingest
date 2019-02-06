package configs

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

trait KafkaConfig {
  this: HasConfig =>

  lazy val host = config.getString("service.kafka.address.host")
  lazy val port = config.getInt("service.kafka.address.port")

  lazy val topicIn  = config.getString("service.kafka.topics.task-input.name")
  lazy val groupIn  = config.getString("service.kafka.topics.task-input.consumer-group")
  lazy val topicOut = config.getString("service.kafka.topics.task-output.name")

  lazy val secs = config.getInt("service.kafka.tick.seconds")

  protected val hostAndPort = s"${host}:${port}"

  protected val kafkaParams = Map[String, Object](
    "bootstrap.servers"  -> hostAndPort,
    "key.deserializer"   -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "key.serializer"     -> classOf[StringSerializer],
    "value.serializer"   -> classOf[StringSerializer],
    "group.id"           -> groupIn,
    "auto.offset.reset"  -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

}
