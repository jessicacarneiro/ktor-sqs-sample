package io.github.jessicacarneiro

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.*
import java.util.*

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

        val queues = mutableListOf<String>()

        SqsClient { region = awsRegion }.use { sqsClient ->
            val response = sqsClient.listQueues(ListQueuesRequest {})

            val queueName = response.queueUrls?.forEach { url ->
                queues.add(url)
            }
        }

        return QueuesResponse(queues.toList())
    }

    suspend fun receiveMessages(queueName: String): QueueMessagesResponse {

        println("Retrieving messages from $queueName")

        val receiveMessageRequest = ReceiveMessageRequest {
            queueUrl = generateQueueUrl(queueName)
            maxNumberOfMessages = 5
        }

        val messages = mutableListOf<QueueMessage>()

        SqsClient { region = awsRegion }.use { sqsClient ->
            val response = sqsClient.receiveMessage(receiveMessageRequest)
            response.messages?.forEach { message ->
                messages.add(QueueMessage(message.body))
            }
        }

        return QueueMessagesResponse(messages.toList())
    }

    suspend fun sendMessage(queueName: String, message: MessageRequest): String {
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

    suspend fun sendBatchMessages(queueName: String, messages: List<Message>): String {
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