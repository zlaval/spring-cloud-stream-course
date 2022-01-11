package com.zlrx.reactive.cloud.course.grademultiplierproducer.stream

import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
    fun produceGradeMultiplier(): Supplier<Flux<GradeMultiplier>> = Supplier {
        generateMessage()
    }

    private fun generateMessage() = Flux.interval(Duration.ofSeconds(2))
        .map {
            GradeMultiplier(
                multiplier = random.nextDouble(10.0, 70.0),
                createdAt = Instant.now()
            )
        }.doOnNext {
            logger.info("Produced value: $it")
        }
}
