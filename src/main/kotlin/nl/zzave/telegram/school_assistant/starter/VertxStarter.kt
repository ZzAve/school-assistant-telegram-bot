package nl.zzave.telegram.school_assistant.starter

import io.vertx.core.Vertx

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(TelegramVerticle())
  vertx.deployVerticle(PeriodicVerticle())
  vertx.deployVerticle(EchoVerticle())
  vertx.deployVerticle(ServerVerticle())

}
