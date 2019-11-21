package org.openmole.core.workspace

import java.util.UUID

import org.openmole.core.workspace.Workspace._
import org.openmole.tool.file._

object NewFile {
  def apply(workspace: Workspace): NewFile = {
    val tmpDirectory = workspace.location / Workspace.tmpLocation /> UUID.randomUUID.toString
    NewFile(tmpDirectory)
  }

  def dispose(newFile: NewFile) = newFile.directory.recursiveDelete
}

case class NewFile(directory: File) {
  def makeNewDir(prefix: String = fixedDir): File = {
    val dir = newDir(prefix)
    dir.mkdirs()
    dir
  }

  def newDir(prefix: String = fixedDir): File = directory.newDir(prefix)
  def newFile(prefix: String = fixedPrefix, suffix: String = fixedPostfix): File = directory.newFile(prefix, suffix)
  def withTmpFile[T](prefix: String, postfix: String)(f: File ⇒ T): T = {
    val file = newFile(prefix, postfix)
    try f(file)
    finally file.delete
  }

  def withTmpFile[T](f: File ⇒ T): T = {
    val file = newFile()
    try f(file)
    finally file.delete
  }

  def withTmpDir[T](f: File ⇒ T): T = {
    val file = newFile()
    try {
      file.mkdir()
      f(file)
    }
    finally file.recursiveDelete
  }
}

