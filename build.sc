import mill._, scalalib._

object backend extends ScalaModule {
  def scalaVersion = "2.13.0"

  def ivyDeps = Agg(ivy"com.typesafe.akka::akka-actor::2.5.25",
                    ivy"com.typesafe.akka::akka-http::10.1.10",
                    ivy"com.typesafe.akka::akka-stream::2.5.25",
                    ivy"com.typesafe:config:1.2.1",
                    ivy"ch.qos.logback:logback-classic:1.2.3"
  )

  def scalacOptions = Seq("-Ydelambdafy:inline", "-deprecation")

}

