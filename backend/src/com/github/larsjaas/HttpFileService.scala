package com.github.larsjaas

import akka.actor.{Actor,Props}
import org.slf4j.{Logger,LoggerFactory}
import akka.http.scaladsl.model._
import scala.util.{Try,Success,Failure}
import scala.io.Source
import java.nio.file.Files
import java.nio.file.Paths

class HttpFileService(basedir: String) extends Actor {
    val log: Logger = HttpFileService.log
    var NAME: String = HttpFileService.NAME

    override def preStart = {
        log.debug("born")
        log.info(s"serving from directory ${basedir}")
    }

    override def postStop = {
        log.debug("died")
    }

    def receive = {
        case req: HttpRequest =>
            Try(onHttpRequest(req)) match {
                case Failure(t: Throwable) =>
                    log.error(s"handling request ${req}", t)
                case _ =>
            }
        case _ =>
            log.error("unknown message")
    }

    def onHttpRequest(req: HttpRequest): Unit = {
        val zender = sender
        val path = basedir + (req.uri.path.toString match {
            case "/" => "/index.html"
            case x => x
        })
        val filename = path.substring(path.lastIndexOf('/')+1)
        val extension = filename.substring(filename.lastIndexOf('.')+1)

        val binaries: Set[String] = Set("png","jpg","ico")

        if (binaries.contains(extension)) {
            Try(Files.readAllBytes(Paths.get(path))) match {
                case Success(data: Array[Byte]) =>
                    zender ! FilePayload(filename, extension, data = Some(data))
                case Failure(t: Throwable) =>
                    zender ! FileError(t.getMessage())
            }
        }
        else {
            Try(Source.fromFile(path, "UTF-8").mkString) match {
                case Success(text: String) =>
                    zender ! FilePayload(filename, extension, text = Some(text))
                case Failure(t: Throwable) =>
                    log.error(s"could not relay ${req.uri.path}")
                    zender ! FileError(t.getMessage())
            }
        }
    }
}

object HttpFileService {
    val NAME = "HttpFileService"
    val log: Logger = LoggerFactory.getLogger(NAME)

    def props(basedir: String): Props = {
        Props(classOf[HttpFileService], basedir)
    }
}

