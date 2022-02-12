package com.zlrx.reactive.cloud.course.grademultiplier.producer.stream

import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.function.Supplier
import kotlin.random.Random

@Configuration
class GradeMultiplierProducer {

    private val random = Random(System.nanoTime())

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun produceGradeMultiplier(): Supplier<Flux<Message<GradeMultiplier>>> = Supplier {
        generateMessage()
    }

    private fun generateMessage() = Flux.interval(Duration.ofSeconds(3))
        .map {
            val multiplier = GradeMultiplier(
                multiplier = random.nextDouble(10.0, 70.0),
                createdAt = Instant.now()
            )

            val key = (it % 2).toString()
            MessageBuilder
                .withPayload(multiplier)
                .setHeader(KafkaHeaders.MESSAGE_KEY, key)
                .build()
        }
}
