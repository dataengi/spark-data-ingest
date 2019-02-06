package errors

import org.scalatest.{FlatSpec, Matchers}
import utils.errors.{ErrorDesc, oError}
import utils.errors.oError.{ErrorDescription, oError}

class ErrorsTest extends FlatSpec with Matchers {

  def errorReturnFunc: oError[Int]  = { oError(ErrorDescription("Some kind of error")) }
  def okReturnFunc: oError[Int]     = { oError(42) }

  "Error description" should "work in for-comprehension" in {

    val result: Either[ErrorDesc, Int] = for {
      a   <-  okReturnFunc.right
      b   <-  errorReturnFunc.right
    } yield  { a + b }

  }

}
