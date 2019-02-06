package utils.errors

import model.TaskID
import play.api.libs.json.{JsValue, Json}

import scala.util.Try

object oError {

  type oError[T] = Either[ErrorDesc, T]


  implicit class oErrorJsonExt(val error: oError[JsValue]) extends AnyVal {
    def toJson (taskID: TaskID): String = {
      error
        .fold(e => Json.obj("jobID" -> taskID.jobID, "result" -> "ERROR", "reason" -> e.description),
          v => Json.obj("jobID" -> taskID.jobID, "result" -> "OK",    "return" -> v))
        .toString
    }
  }

  implicit class oErrorExt[T] (val error: oError[T]) extends AnyVal {
    def toJson (taskID: TaskID): String = {
      error
        .fold (e => Json.obj("jobID" -> taskID.jobID, "result" -> "ERROR", "reason" -> e.description),
               v => Json.obj("jobID" -> taskID.jobID, "result" -> "OK",    "return"  -> v.toString))
        .toString
    }
  }

  case class ErrorDescription (message: String) extends ErrorDesc {
    override def description: String = message
  }

  def apply[T](value: T): oError[T]         = Right(value)
  def apply[T](error: ErrorDesc): oError[T] = Left(error)
  def left[T](error: ErrorDesc): oError[T]  = Left(error)

  def handleException[T](block: => T): oError[T] = {
    Try {
      Right(block)
    }.recover {
      case e:Exception =>
        Left(ErrorDescription(e.getMessage))
    }.get
  }

  def oErrorUnit: oError[Unit] = oError((): Unit)

}
