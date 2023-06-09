package io.github.jessicacarneiro.service

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.*
import io.github.jessicacarneiro.models.BatchMessage
import io.github.jessicacarneiro.models.Message
import io.github.jessicacarneiro.response.QueueMessage
import io.github.jessicacarneiro.response.QueueMessagesResponse
import io.github.jessicacarneiro.response.QueuesResponse

class SqsQueueService {
    private val awsRegion = System.getenv("AWS_REGION")
    private val awsAccountId = System.getenv("AWS_ACCOUNT_ID")
    private val queueBaseUrl = "https://sqs.$awsRegion.amazonaws.com"

    suspend fun createQueue(queueNameVal: String): String {

        println("Create Queue")
        val createQueueRequest = CreateQueueRequest {
            queueName = queueNameVal
        }

        SqsClient { region = awsRegion }.use { sqsClient ->
            sqsClient.createQueue(createQueueRequest)
            println("Get queue url")

            val getQueueUrlRequest = GetQueueUrlRequest {
                queueName = queueNameVal
            }

            val getQueueUrlResponse = sqsClient.getQueueUrl(getQueueUrlRequest)
            return getQueueUrlResponse.queueUrl.toString()
        }
    }

    suspend fun deleteMessages(queueName: String): String {

        println("Delete Messages from $queueName")

        val purgeRequest = PurgeQueueRequest {
            queueUrl = generateQueueUrl(queueName)
        }

        SqsClient { region = awsRegion }.use { sqsClient ->
            sqsClient.purgeQueue(purgeRequest)
            return "Messages are successfully deleted from $queueName"
        }
    }

    suspend fun deleteQueue(queueName: String): String {

        val request = DeleteQueueRequest {
            queueUrl = generateQueueUrl(queueName)
        }

        SqsClient { region = awsRegion }.use { sqsClient ->
            sqsClient.deleteQueue(request)
            return "$queueName was deleted!"
        }
    }

    suspend fun listQueues(): QueuesResponse {

        println("\nList Queues")

        SqsClient { region = awsRegion }.use { sqsClient ->
            val response = sqsClient.listQueues(ListQueuesRequest {})

            return QueuesResponse(response.queueUrls)
        }
    }

    suspend fun receiveMessages(queueName: String): QueueMessagesResponse {

        println("Retrieving messages from $queueName")

        val receiveMessageRequest = ReceiveMessageRequest {
            queueUrl = generateQueueUrl(queueName)
            maxNumberOfMessages = 5
        }

        SqsClient { region = awsRegion }.use { sqsClient ->
            val response = sqsClient.receiveMessage(receiveMessageRequest)

            return QueueMessagesResponse(response.messages?.map { message ->
                QueueMessage(
                    message.messageId,
                    message.body,
                )
            } ?: emptyList())
        }
    }

    suspend fun sendMessage(queueName: String, message: Message): String {

        println("Sending a message")

        val messageRequest = SendMessageRequest {
            queueUrl = generateQueueUrl(queueName)
            messageBody = message.body
        }

        SqsClient { region = awsRegion }.use { sqsClient ->
            sqsClient.sendMessage(messageRequest)
            return "Message was successfully sent."
        }
    }

    suspend fun sendBatchMessages(queueName: String, messages: List<BatchMessage>): String {

        println("Sending multiple messages")

        val messagesToSend = messages.map { message ->
            SendMessageBatchRequestEntry {
                id = message.id
                messageBody = message.body
            }
        }

        val sendMessageBatchRequest = SendMessageBatchRequest {
            queueUrl = generateQueueUrl(queueName)
            entries = messagesToSend
        }

        SqsClient { region = awsRegion }.use { sqsClient ->
            sqsClient.sendMessageBatch(sendMessageBatchRequest)
            return "Batch message were successfully sent."
        }
    }

    private fun generateQueueUrl(queueName: String) = "$queueBaseUrl/$awsAccountId/$queueName"
}