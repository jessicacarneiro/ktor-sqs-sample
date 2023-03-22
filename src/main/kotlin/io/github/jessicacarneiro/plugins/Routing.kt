package io.github.jessicacarneiro.plugins

import io.github.jessicacarneiro.CreateQueueRequest
import io.github.jessicacarneiro.MessageRequest
import io.github.jessicacarneiro.SqsQueueService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val sqsQueueService = SqsQueueService()
fun Application.configureRouting() {
    routing {
        get("/queues") {
            call.respond(sqsQueueService.listQueues())
        }

        post("/queues") {
            val request = call.receive<CreateQueueRequest>()
            call.respond(sqsQueueService.createQueue(request.queueName))
        }

        get("/queues/{queue}/messages") {
            val queueName = call.parameters["queue"]
            call.respond(sqsQueueService.receiveMessages(queueName ?: System.getenv("DEFAULT_QUEUE_NAME")))
        }

        post("queues/{queue}/messages") {
            val queueName = call.parameters["queue"]
            val request = call.receive<MessageRequest>()

            call.respond(sqsQueueService.sendMessage(queueName?: System.getenv("DEFAULT_QUEUE_NAME"), request))
        }

        delete("queues/{queue}") {
            val queueName = call.parameters["queue"]
            call.respond(sqsQueueService.deleteQueue(queueName!!))
        }

        delete("queues/{queue}/messages") {
            val queueName = call.parameters["queue"]
            call.respond(sqsQueueService.deleteMessages(queueName!!))
        }
    }
}
