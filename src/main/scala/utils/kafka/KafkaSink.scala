package utils.kafka

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConverters._

class KafkaSink(createProducer: () => KafkaProducer[String, String]) extends Serializable {

  lazy val producer = createProducer()

  def send(topic: String, value: String): Unit = producer.send(new ProducerRecord(topic, value))
}

object KafkaSink {
  def apply(config: Map[String, Object]): KafkaSink = {
    new KafkaSink(() => {
      val producer = new KafkaProducer[String, String](config.asJava)

      sys.addShutdownHook {
        producer.close()
      }

      producer
    })
  }

}
