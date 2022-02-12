package com.zlrx.reactive.student.processor.listener

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.binder.kafka.KafkaBindingRebalanceListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CustomBinderListener {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun kafkaRebalanceListener(): KafkaBindingRebalanceListener = object : KafkaBindingRebalanceListener {

        override fun onPartitionsAssigned(bindingName: String?, consumer: Consumer<*, *>?, partitions: MutableCollection<TopicPartition>?, initial: Boolean) {
            super.onPartitionsAssigned(bindingName, consumer, partitions, initial)
            logger.info("Partitions $partitions were assigned to the $bindingName")
        }
    }
}
