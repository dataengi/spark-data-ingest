package helpers

import model.FileInfo
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}

object InDirFiles {

  private val defaultDelimiter = "#"

  def extractMetadataFromFilename (file: FileStatus, delimiter: String = defaultDelimiter): Option[FileInfo] = {
    val fname = file.getPath.getName
    val p = fname.split(delimiter)

    try {
      Some(FileInfo(p(0), p(2), p(1), p(3), p(4), file.getPath.toString))
    } catch {
      case _:Throwable => None
    }
  }


  def getFilesList (fs: FileSystem, directory: String) = {
    fs.listStatus(new Path(directory))
  }

}
