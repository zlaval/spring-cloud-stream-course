package com.zlrx.reactive.cloud.course.grademultiplier.consumer.stream

import com.zlrx.reactive.cloud.course.grademultiplier.consumer.service.GradeService
import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

@Configuration
class GradeMultiplierConsumer(
    private val gradeService: GradeService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val counter = AtomicInteger(0)

    private val errorProcessor = Sinks.many().unicast().onBackpressureBuffer<Error>()

    @Bean
    fun consumeGradeMultiplier(): Function<Flux<Message<GradeMultiplier>>, Flux<Double>> = Function { stream ->
        stream
            .doOnNext {
                val headers = it.headers
                val partition = headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer::class.java)
                // logger.info("Partition: $partition")
            }
            .map { it.payload }
            .doOnNext {
                if (it.multiplier < 30) {
                    logger.error("Processing GradeMultiplier has failed, the value (${it.multiplier}) is lower than 30")
                    throw RuntimeException("Processing GradeMultiplier has failed, the value (${it.multiplier}) is lower than 30")
                }
            }
            .doOnNext {
                gradeService.emmitMessage(it)
                // logger.info("Actual grade-multiplier: $it")
            }.map {
                it.multiplier
            }.doOnNext {
                val index = counter.incrementAndGet()
                logger.info("$index. message: $it is received")
            }.onErrorContinue { t, u ->
                logger.info("Continue on error. $u. Error: $t")
                errorProcessor.emitNext(
                    Error(t.message, u),
                    Sinks.EmitFailureHandler.FAIL_FAST
                )
            }
    }

    @Bean
    fun consumeGradeValue(): Consumer<Flux<Double>> = Consumer { stream ->
        stream.doOnNext {
            // logger.info("Actual grade-multiplier value: $it")
        }.subscribe()
    }

    @Bean
    fun gradeDlq(): Supplier<Flux<Error>> = Supplier {
        errorProcessor.asFlux()
    }

    data class Error(
        val error: String?,
        val message: Any?
    )
}
