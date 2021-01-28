package nl.zzave.telegram.school_assistant.starter

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Launcher
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.MongoClientDeleteResult


class MongoClientVerticle : AbstractVerticle() {
  @Throws(Exception::class)
  override fun start() {
    val config = Vertx.currentContext().config()
    var uri = config.getString("mongo_uri")
    if (uri == null) {
      uri = "mongodb://localhost:27017"
    }
    var db = config.getString("mongo_db")
    if (db == null) {
      db = "test"
    }
    val mongoconfig = JsonObject()
      .put("connection_string", uri)
      .put("db_name", db)
    val mongoClient = MongoClient.createShared(vertx, mongoconfig)
    val product1 = JsonObject().put("itemId", "12345").put("name", "Cooler").put("price", "100.0")
    mongoClient.save("products", product1)
      .compose { id: String ->
        println("Inserted id: $id")
        mongoClient.find("products", JsonObject().put("itemId", "12345"))
      }
      .compose { res: List<JsonObject> ->
        println("Name is " + res[0].getString("name"))
        mongoClient.removeDocument("products", JsonObject().put("itemId", "12345"))
      }
      .onComplete { ar: AsyncResult<MongoClientDeleteResult> ->
        if (ar.succeeded()) {
          println("Product removed ")
        } else {
          ar.cause().printStackTrace()
        }
      }
  }

}

fun main(args: Array<String>) {
  Launcher.executeCommand("run", MongoClientVerticle::class.java.name)
}
