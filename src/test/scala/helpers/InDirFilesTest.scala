package helpers

import com.ImportBatch
import model.FileInfo
import org.apache.hadoop.fs.{FileStatus, Path}
import org.scalatest.{FlatSpec, Matchers}

class InDirFilesTest extends FlatSpec  with Matchers {

  private val correctName   = buildPath("provider#UA#UA_files#1530011462#CH001JUNE2018.csv")
  private val incorrectName = buildPath("provider#UA#UA_files1530011462#CH001JUNE2018.csv")

  def buildPath (filename: String): FileStatus = {
    val filestat = new FileStatus()
    filestat.setPath(new Path (filename))
    filestat
  }

  "extractMetadata" should "return correct MetaData object" in {

    val md = InDirFiles.extractMetadataFromFilename(correctName).getOrElse(null)

    md should not be null

    md.dataprovider   shouldBe "provider"
    md.dataprofile    shouldBe "UA_files"
    md.country        shouldBe "UA"
    md.date           shouldBe "1530011462"
    md.filename       shouldBe "CH001JUNE2018.csv"
    md.filepath       shouldBe "provider#UA#UA_files#1530011462#CH001JUNE2018.csv"

  }

  it should "return None" in {

    val md = InDirFiles.extractMetadataFromFilename(incorrectName)

    md shouldBe None

  }

  it should "skip incorrect files" in {
    val list = Array (
      correctName, incorrectName, correctName
    )
    val metadata: Array[FileInfo] = ImportBatch.extractMetadata(list)

    metadata should have length 2
  }

}
