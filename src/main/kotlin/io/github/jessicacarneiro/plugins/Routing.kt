package io.github.jessicacarneiro.plugins

import io.github.jessicacarneiro.request.CreateQueueRequest
import io.github.jessicacarneiro.request.SendMessageRequest
import io.github.jessicacarneiro.request.SendMessagesBatchRequest
import io.github.jessicacarneiro.service.SqsQueueService
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
            val request = call.receive<SendMessageRequest>()

            call.respond(sqsQueueService.sendMessage(queueName ?: System.getenv("DEFAULT_QUEUE_NAME"), request.message))
        }

        post("queues/{queue}/messages/batch") {
            val queueName = call.parameters["queue"]
            val request = call.receive<SendMessagesBatchRequest>()

            call.respond(
                sqsQueueService.sendBatchMessages(
                    queueName ?: System.getenv("DEFAULT_QUEUE_NAME"),
                    request.messages
                )
            )
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
