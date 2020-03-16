package org.openmole.core

import au.com.bytecode.opencsv.CSVReader

/*
 * Copyright (C) 2019 Romain Reuillon
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
package object csv {
  import java.io.{ FileReader, PrintStream }
  import java.math.{ BigDecimal, BigInteger }

  import org.openmole.core.context.{ Context, Val, Variable }
  import org.openmole.core.exception.UserBadDataError
  import org.openmole.core.tools.io.Prettifier._
  import org.openmole.tool.file._
  import org.openmole.tool.stream._

  import scala.annotation.tailrec

  def moreThanOneElement(l: List[_]) = !l.isEmpty && !l.tail.isEmpty

  object CSVData {
    def toList(d: CSVData) =
      d match {
        case d: ArrayData  ⇒ d.v
        case s: ScalarData ⇒ List(s.v)
      }

  }

  sealed trait CSVData
  case class ScalarData(v: Any) extends CSVData
  case class ArrayData(v: List[Any]) extends CSVData

  def valuesToData(values: Seq[Any]) =
    values.map {
      case v: Array[_] ⇒ ArrayData(v.toList)
      case l: List[_]  ⇒ ArrayData(l)
      case v           ⇒ ScalarData(v)
    }.toList

  def header(prototypes: Seq[Val[_]], values: Seq[Any], arraysOnSingleRow: Boolean = false) = {
    val lists =
      values.map {
        case v: Array[_] ⇒ v.toList
        case l: List[_]  ⇒ l
        case v           ⇒ List(v)
      }.toList

    (prototypes zip lists).flatMap {
      case (p, l) ⇒
        if (arraysOnSingleRow && moreThanOneElement(l))
          (0 until l.size).map(i ⇒ s"${p.name}$i")
        else List(p.name)
    }.mkString(",")
  }

  def writeVariablesToCSV(
    output:            PrintStream,
    header:            ⇒ Option[String] = None,
    values:            Seq[Any],
    arraysOnSingleRow: Boolean          = false): Unit = {
    header.foreach(h ⇒ output.appendLine { h })

    // TODO add option to flatten multidim arrays here be be written on multiple lines

    def flatAny(o: Any): List[Any] = o match {
      case o: List[_] ⇒ o
      case _          ⇒ List(o)
    }

    def writeData(data: List[CSVData]): Unit = {
      val scalars = data.collect { case x: ScalarData ⇒ x }
      if (scalars.size == data.size) writeLine(scalars.map(_.v))
      else if (arraysOnSingleRow) {
        val lists = data.map(CSVData.toList)
        writeLine(lists.flatten(flatAny))
      }
      else writeArrayData(data)
    }

    @tailrec def writeArrayData(data: List[CSVData]): Unit = {
      if (data.collect { case l: ArrayData ⇒ l }.forall(_.v.isEmpty)) Unit
      else {
        val lists = data.map(CSVData.toList)
        writeLine(lists.map { _.headOption.getOrElse("") })

        def tail(d: CSVData) =
          d match {
            case a @ ArrayData(Nil) ⇒ a
            case a: ArrayData       ⇒ a.copy(a.v.tail)
            case s: ScalarData      ⇒ s
          }

        writeArrayData(data.map(tail))
      }
    }

    def writeLine[T](list: List[T]) = {
      output.appendLine(list.map(l ⇒ {
        val prettified = l.prettify()
        def shouldBeQuoted = prettified.contains(',') || prettified.contains('"')
        def quote(s: String) = '"' + s.replaceAll("\"", "\"\"") + '"'
        if (shouldBeQuoted) quote(prettified) else prettified
      }).mkString(","))
    }

    writeData(valuesToData(values))
  }

  /**
   * Builds the plan.
   *
   */
  def csvToVariables(
    file:      File,
    columns:   Seq[(String, Val[_])],
    separator: Option[Char]          = None): Iterator[Iterable[Variable[_]]] = {
    val reader = new CSVReader(new FileReader(file), separator.getOrElse(','))
    val headers = reader.readNext.toArray

    val columnsIndexes = columns.map {
      case (name, _) ⇒
        val i = headers.indexOf(name)
        if (i == -1) throw new UserBadDataError("Unknown column name : " + name)
        else i
    }

    Iterator.continually(reader.readNext).takeWhile(_ != null).map { line ⇒
      (columns zip columnsIndexes).map {
        case ((_, v), i) ⇒ Variable.unsecure(v, converter(v)(line(i)))
      }
    }
  }

  val conveters = Map[Class[_], (String ⇒ _)](
    classOf[BigInteger] → (new BigInteger(_: String)),
    classOf[BigDecimal] → (new BigDecimal(_: String)),
    classOf[Double] → ((_: String).toDouble),
    classOf[String] → ((_: String).toString),
    classOf[Boolean] → ((_: String).toBoolean),
    classOf[Int] → ((_: String).toInt),
    classOf[Float] → ((_: String).toFloat),
    classOf[Long] → ((_: String).toLong)
  )

  def converter[T](p: Val[_]): String ⇒ _ =
    conveters.getOrElse(p.`type`.runtimeClass, throw new UserBadDataError("Unmanaged type for csv sampling for column binded to prototype " + p))

}
