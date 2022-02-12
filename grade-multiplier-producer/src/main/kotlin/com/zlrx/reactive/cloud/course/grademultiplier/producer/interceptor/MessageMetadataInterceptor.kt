package com.zlrx.reactive.cloud.course.grademultiplier.producer.interceptor

import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message

@Configuration
class MessageMetadataInterceptor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ServiceActivator(inputChannel = "producerMetaData")
    fun producerMetaData(msg: Message<*>) {
        val metadata = msg.headers.get(KafkaHeaders.RECORD_METADATA, RecordMetadata::class.java)
        metadata?.let {
            logger.info("Message was sent to ${it.topic()}. Partition: ${it.partition()}. Offset: ${it.offset()}")
        }
    }
}
