package nl.zzave.telegram.school_assistant.starter

import io.vertx.core.AbstractVerticle
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject

class PeriodicVerticle : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  var timerId: Long = -1
  override fun start() {
    super.start()
    log.info("Started $javaClass")

    timerId = vertx.setPeriodic(10000) { id: Long? ->
      vertx.eventBus().publish(TelegramVerticle.ADDRESS, JsonObject().put("id", id).put("tomatoe", "tomahto"))
    }
  }

  override fun stop() {
    super.stop()
    vertx.cancelTimer(timerId)
  }
}
