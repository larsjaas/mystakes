package com.github.larsjaas

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import org.slf4j.{Logger,LoggerFactory}
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.Timeout
import scala.concurrent.Future
import akka.http.scaladsl.server.Directives._
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import scala.concurrent.duration._
import scala.util.{Success,Failure}
import akka.pattern.ask
import scala.language.postfixOps


class WebService(port: Int, fileService: ActorRef, conductor: ActorRef) extends Actor {
    val log: Logger = WebService.log
    var NAME: String = WebService.NAME

    var bindingFuture: Future[ServerBinding] = _

    implicit val system = context.system

    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    
    override def preStart = {
        log.debug("born")

        implicit val timeout = Timeout(30.seconds)

        val route = 
            get {
                extractRequest { req =>
                    pathPrefix("api") {
                        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>API</h1>"))
                    } ~
                    path("admin" / "quit") {
                        conductor ! Terminate
                        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>terminating</h1>"))
                    } ~
                    onComplete(fileService ? req) {
                        case Success(result: FilePayload) =>
                            log.info(s"serving ${result.filename}")
                            result.extension match {
                                case "html" =>
                                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result.text.get))
                                case "js" =>
                                    complete(HttpEntity(MediaTypes.`application/javascript`.withCharset(HttpCharsets.`UTF-8`), result.text.get))
                                case "map" =>
                                    complete(HttpEntity(ContentTypes.`application/octet-stream`, result.text.get.getBytes()))
                                case "png" =>
                                    complete(HttpEntity(MediaTypes.`image/png`, result.data.get))
                                case "ico" =>
                                    complete(HttpEntity(MediaTypes.`image/x-icon`, result.data.get))
                                case _ =>
                                    complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, result.text.get))

                            }
                        case Success(error: FileError) =>
                            log.error(error.message)
                            complete(HttpResponse(status = StatusCodes.NotFound))
                        case Success(x) =>
                            complete(HttpResponse(status = StatusCodes.NotImplemented))
                        case _ =>
                            complete(HttpResponse(status = StatusCodes.Forbidden))
                    }
                }
            }

        bindingFuture = Http().bindAndHandle(route, "localhost", port)

        log.info(s"service online at http://localhost:${port}/")
    }

    override def postStop = {
        log.debug("died")

        bindingFuture
          .flatMap(_.unbind()) // trigger unbinding from the port
          .onComplete(_ => system.terminate()) // and shutdown when done
    }

    def receive = {
        case x =>
            log.info(s"unknown message $x")
    }
}

object WebService {
    val NAME = "WebService"
    val log = LoggerFactory.getLogger(NAME)

    def props(port: Int, fileService: ActorRef, conductor: ActorRef): Props = {
        Props(classOf[WebService], port, fileService, conductor)
    }
}