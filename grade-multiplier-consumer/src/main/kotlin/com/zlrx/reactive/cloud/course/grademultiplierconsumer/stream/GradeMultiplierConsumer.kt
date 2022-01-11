package com.zlrx.reactive.cloud.course.grademultiplierconsumer.stream

import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Consumer

@Configuration
class GradeMultiplierConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun consumeGradeMultiplier(): Consumer<Flux<GradeMultiplier>> = Consumer { stream ->
        stream.map {
            it.multiplier
        }.doOnNext {
            logger.info("Actual multiplier: $it")
        }.subscribe()
    }
}
