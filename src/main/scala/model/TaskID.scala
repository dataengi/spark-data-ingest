package model

case class TaskID(jobID: Int)

object TaskID {
  val none = TaskID(-1)
}