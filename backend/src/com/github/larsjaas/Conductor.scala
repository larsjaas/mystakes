package com.github.larsjaas

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import org.slf4j.{Logger,LoggerFactory}
import com.typesafe.config.{Config,ConfigFactory}
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration


class Conductor extends Actor {
    val log = Conductor.log

    var webService: ActorRef = _
    var fileService: ActorRef = _

    override def preStart = {
        log.debug("born")
        val config = ConfigFactory.load()
            .resolveWith(ConfigFactory.systemEnvironment())
            .resolveWith(ConfigFactory.systemProperties())
            .resolve();

        val port = config.getInt("web.port")
        val basedir = config.getString("web.server.root")

        val system: ActorSystem = context.system
        fileService = system.actorOf(HttpFileService.props(basedir), HttpFileService.NAME)
        webService = system.actorOf(WebService.props(port, fileService, self), WebService.NAME)
    }

    override def postStop = {
        log.debug("died")
    }

    def receive = {
        case Terminate =>
            onTerminate()
        case x =>
            log.warn(s"unknown message $x")
    }

    def onTerminate(): Unit = {
        implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
        context.system.scheduler.scheduleOnce(FiniteDuration(500, TimeUnit.MILLISECONDS)) {
            context.system.terminate()
        }
    }
}

object Conductor {
    val NAME: String = "Conductor"
    val log = LoggerFactory.getLogger(NAME)

    val system: ActorSystem = ActorSystem("mystakes")

    def props(): Props = {
        Props.create(classOf[Conductor])
    }

    def main(args: Array[String]): Unit = {
        system.actorOf(props(), NAME)
    }
}