package nl.zzave.telegram.school_assistant.starter

import io.vertx.core.AbstractVerticle
import io.vertx.core.impl.logging.LoggerFactory

class EchoVerticle : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start(){
    log.info("Started $javaClass")

    vertx.eventBus().consumer<String>(ADDRESS){ msg ->
      log.info("Received a message: ${msg.body()}")
      msg.reply("Echo: ${msg.body()}")
    }
  }

  companion object{
    const val ADDRESS = "echo-service"
  }
}
