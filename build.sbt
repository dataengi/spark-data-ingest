name := "Spark-Data-Import"

organization := "com.dataengi"

version := "0.1"

scalaVersion := "2.11.12"

scalacOptions ++= Seq("-unchecked",
                      "-deprecation",
                      "-feature",
                      "-encoding",
                      "UTF-8",
                      "-target:jvm-1.8",
                      "-Ywarn-dead-code",
                      "-Xlog-reflective-calls",
                      "-Xlint")

libraryDependencies ++= Dependencies.core
libraryDependencies ++= Dependencies.spark
libraryDependencies ++= Dependencies.streaming
libraryDependencies ++= Dependencies.playJson
libraryDependencies ++= Dependencies.tests

parallelExecution in Test := false
fork in Test := true

// include assembly as artifact for artifactory
artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)
isSnapshot := true
publishTo := Some(Artifactory.realm at Artifactory.url)
credentials += Credentials(new File(Artifactory.credentials))
