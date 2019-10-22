package com.github

package object larsjaas {
    case object Terminate

    case class FilePayload(filename: String, extension: String, text: Option[String] = None, data: Option[Array[Byte]] = None)
    case class FileError(message: String)
}
