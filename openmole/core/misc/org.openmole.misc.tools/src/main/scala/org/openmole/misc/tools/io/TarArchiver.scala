/*
 * Copyright (C) 2010 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.misc.tools.io

import java.security.{ PrivilegedAction, AccessController }

import com.ice.tar.TarEntry
import com.ice.tar.TarInputStream
import com.ice.tar.TarOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack
import org.openmole.misc.tools.io.FileUtil._
import java.nio.file.FileSystems
import java.nio.file.Files

object TarArchiver {

  implicit def TarInputStream2TarInputStreamDecorator(tis: TarInputStream) = new TarInputStreamDecorator(tis)
  implicit def TarOutputStream2TarOutputStreamComplement(tos: TarOutputStream) = new TarOutputStreamDecorator(tos)

  class TarOutputStreamDecorator(tos: TarOutputStream) {

    def addFile(f: File, name: String) = {
      val entry = new TarEntry(name)
      entry.setSize(f.length)
      entry.setMode(f.mode)
      tos.putNextEntry(entry)
      try f.copy(tos) finally tos.closeEntry
    }

    def createDirArchiveWithRelativePathNoVariableContent(baseDir: File) = createDirArchiveWithRelativePathWithAdditionnalCommand(tos, baseDir, (e: TarEntry) ⇒ e.setModTime(0))
    def createDirArchiveWithRelativePath(baseDir: File) = createDirArchiveWithRelativePathWithAdditionnalCommand(tos, baseDir, { (e) ⇒ })
  }

  class TarInputStreamDecorator(tis: TarInputStream) {

    def applyAndClose[T](f: TarEntry ⇒ T): Iterable[T] = try {
      val ret = new ListBuffer[T]

      var e = tis.getNextEntry
      while (e != null) {
        ret += f(e)
        e = tis.getNextEntry
      }
      ret
    }
    finally tis.close

    def extractDirArchiveWithRelativePath(baseDir: File) = {
      if (!baseDir.isDirectory) throw new IOException(baseDir.getAbsolutePath + " is not a directory.")

      val links = Iterator.continually(tis.getNextEntry).takeWhile(_ != null).flatMap {
        e ⇒
          val dest = new File(baseDir, e.getName)
          val link =
            if (!e.getLinkName.isEmpty) Some(dest -> e.getLinkName)
            else if (e.isDirectory) {
              dest.mkdirs
              None
            }
            else {
              dest.getParentFile.mkdirs
              dest.withOutputStream(tis.copy)
              None
            }
          dest.mode = e.getMode
          link
      }.toList

      links.foreach {
        case ((dest, name)) ⇒ dest.createLink(name)
      }

    }
  }

  private def createDirArchiveWithRelativePathWithAdditionnalCommand(tos: TarOutputStream, baseDir: File, additionnalCommand: TarEntry ⇒ Unit) = {
    if (!baseDir.isDirectory) throw new IOException(baseDir.getAbsolutePath + " is not a directory.")

    val fs = FileSystems.getDefault
    val toArchive = new Stack[(File, String)]
    toArchive.push((baseDir, ""))

    var links = List.empty[(File, String)]

    while (!toArchive.isEmpty) {
      val (source, entryName) = toArchive.pop
      if (Files.isSymbolicLink(fs.getPath(source.getAbsolutePath))) links ::= source -> entryName
      else {
        val e =
          if (source.isDirectory) {
            for (name ← source.list.sorted) toArchive.push((new File(source, name), entryName + '/' + name))
            new TarEntry(entryName + '/')
          }
          else {
            val e = new TarEntry(entryName)
            e.setSize(source.length)
            e
          }
        e.setMode(source.mode)
        additionnalCommand(e)
        tos.putNextEntry(e)
        if (!source.isDirectory) try source.copy(tos) finally tos.closeEntry
      }
    }

    links.foreach {
      case (source, entryName) ⇒
        val e = new TarEntry(entryName)
        e.setLinkName(
          fs.getPath(source.getParentFile.getCanonicalPath).relativize(fs.getPath(source.getCanonicalPath)).toString
        )
        e.setMode(source.mode)
        tos.putNextEntry(e)
    }
  }

}
