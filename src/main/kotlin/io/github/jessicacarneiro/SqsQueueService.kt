package io.github.jessicacarneiro

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.*

class SqsQueueService {
    suspend fun createQueue(queueNameVal: String): String {

        println("Create Queue")
        val createQueueRequest = CreateQueueRequest {
            queueName = queueNameVal
        }

        SqsClient { region = "us-east-1" }.use { sqsClient ->
            sqsClient.createQueue(createQueueRequest)
            println("Get queue url")

            val getQueueUrlRequest = GetQueueUrlRequest {
                queueName = queueNameVal
            }

            val getQueueUrlResponse = sqsClient.getQueueUrl(getQueueUrlRequest)
            return getQueueUrlResponse.queueUrl.toString()
        }
    }

    suspend fun deleteMessages(queueUrlVal: String) {
        println("Delete Messages from $queueUrlVal")

        val purgeRequest = PurgeQueueRequest {
            queueUrl = queueUrlVal
        }

        SqsClient { region = "us-east-1" }.use { sqsClient ->
            sqsClient.purgeQueue(purgeRequest)
            println("Messages are successfully deleted from $queueUrlVal")
        }
    }

    suspend fun deleteQueue(queueUrlVal: String) {

        val request = DeleteQueueRequest {
            queueUrl = queueUrlVal
        }

        SqsClient { region = "us-east-1" }.use { sqsClient ->
            sqsClient.deleteQueue(request)
            println("$queueUrlVal was deleted!")
        }
    }

    suspend fun listQueues() {
        println("\nList Queues")

        val prefix = "que"
        val listQueuesRequest = ListQueuesRequest {
            queueNamePrefix = prefix
        }

        SqsClient { region = "us-east-1" }.use { sqsClient ->
            val response = sqsClient.listQueues(listQueuesRequest)
            response.queueUrls?.forEach { url ->
                println(url)
            }
        }
    }

    suspend fun receiveMessages(queueUrlVal: String?) {

        println("Retrieving messages from $queueUrlVal")

        val receiveMessageRequest = ReceiveMessageRequest {
            queueUrl = queueUrlVal
            maxNumberOfMessages = 5
        }

        SqsClient { region = "us-east-1" }.use { sqsClient ->
            val response = sqsClient.receiveMessage(receiveMessageRequest)
            response.messages?.forEach { message ->
                println(message.body)
            }
        }
    }
}