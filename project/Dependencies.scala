import sbt._

object Version {
  val spark     = "2.2.0"
  val typesafe  = "1.3.3"
  val playJson  = "2.6.9"
  
  // Test deps
  val scalactic = "3.0.1"
  val specs2    = "4.3.0"
}

object Dependencies {
  val core = Seq(
    "com.typesafe" % "config" % Version.typesafe
  )

  val spark = Seq (
    "org.apache.spark" %% "spark-core"  % Version.spark % Provided withSources(),
    "org.apache.spark" %% "spark-sql"   % Version.spark % Provided withSources()
  )

  val streaming = Seq (
    "org.apache.spark" %% "spark-streaming"            % Version.spark % Provided withSources(),
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % Version.spark % Provided withSources()
  )

  val tests = Seq (
    "org.scalatest"  %% "scalatest"      % Version.scalactic % Test,
    "org.specs2"     %% "specs2-core"    % Version.specs2    % Test
  )

  val playJson = Seq (
    "com.typesafe.play" %% "play-json" % Version.playJson
  )
}