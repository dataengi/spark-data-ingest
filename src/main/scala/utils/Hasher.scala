package utils

import java.security.MessageDigest

trait Hasher {

  def md5(s: String): String =
    MessageDigest.getInstance("MD5").digest(s.getBytes).map(0xFF & _).map {"%02x".format(_)}.foldLeft("") {_ + _ }

  def hashToData(data: Seq[String]) =
    Map(data map { a => md5(a) -> a }: _*)

}
