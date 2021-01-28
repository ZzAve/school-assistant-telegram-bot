package nl.zzave.telegram.school_assistant.starter

import com.elbekD.bot.Bot
import com.elbekD.bot.feature.chain.chain
import io.vertx.core.AbstractVerticle
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject

class TelegramVerticle : AbstractVerticle() {

  private var bot: Bot? = null
  private val chats: MutableSet<Long> = mutableSetOf()

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    log.info("Starting $javaClass")
    val bot: Bot = configureTelegramBot()
    bot.start()
    this.bot = bot

    vertx.eventBus().consumer<JsonObject>(ADDRESS) { m ->
      val body = m.body()
      log.info("Received message: $body")

      chats.forEach {
        bot.sendMessage(it, "Received message: $body")
      }
    }

  }

  private fun configureTelegramBot(): Bot {
    val token = "<token>"
    val username = "<username>"
    val bot: Bot = Bot.createPolling(username, token)

    bot.chain("/start") { msg -> bot.sendMessage(msg.chat.id, "Hi! What is your name?") }
      .then { msg -> bot.sendMessage(msg.chat.id, "Nice to meet you, ${msg.text}! Send something to me") }
      .then { msg ->
        bot.sendMessage(msg.chat.id, "Fine! See you soon")
        chats.add(msg.chat.id)
      }
      .build()

    bot.chain("/echo") { msg -> bot.sendMessage(msg.chat.id, "What do you want me to echo?") }
      .then { msg ->
        log.info("Received an echo message ${msg.chat.id}: ${msg.text}")
        vertx.eventBus().request<String>(EchoVerticle.ADDRESS, msg.text) { ar ->
          log.info("Reply from echo: $ar")
          if (ar.succeeded()) {
            bot.sendMessage(msg.chat.id, ar.result().body())
          }
        }
      }
      .build()

    bot.chain("/stop") { msg ->
      chats.remove(msg.chat.id)
      bot.sendMessage(msg.chat.id, "You will no longer receive periodic updates")
    }
      .build()

    return bot
  }

  override fun stop() {
    super.stop()
    if (bot != null) bot!!.stop()
  }


  companion object {
    const val ADDRESS = "telegram-service"
  }
}
