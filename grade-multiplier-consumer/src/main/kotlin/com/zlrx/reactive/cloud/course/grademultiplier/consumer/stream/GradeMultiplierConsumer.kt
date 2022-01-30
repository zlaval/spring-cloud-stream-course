package com.zlrx.reactive.cloud.course.grademultiplier.consumer.stream

import com.zlrx.reactive.cloud.course.grademultiplier.consumer.service.GradeService
import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import java.util.function.Consumer
import java.util.function.Function

@Configuration
class GradeMultiplierConsumer(
    private val gradeService: GradeService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun consumeGradeMultiplier(): Function<Flux<Message<GradeMultiplier>>, Flux<Double>> = Function { stream ->
        stream
            .doOnNext {
                val headers = it.headers
                val partition = headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer::class.java)
                logger.info("Partition: $partition")
            }
            .map { it.payload }
            .doOnNext {
                gradeService.emmitMessage(it)
                // logger.info("Actual grade-multiplier: $it")
            }.map {
                it.multiplier
            }
    }

    @Bean
    fun consumeGradeValue(): Consumer<Flux<Double>> = Consumer { stream ->
        stream.doOnNext {
            // logger.info("Actual grade-multiplier value: $it")
        }.subscribe()
    }
}
