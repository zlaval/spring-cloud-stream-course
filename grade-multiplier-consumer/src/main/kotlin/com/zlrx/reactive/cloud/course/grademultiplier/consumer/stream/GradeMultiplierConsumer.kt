package com.zlrx.reactive.cloud.course.grademultiplier.consumer.stream

import com.zlrx.reactive.cloud.course.grademultiplier.consumer.service.GradeService
import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Consumer

@Configuration
class GradeMultiplierConsumer(
    private val gradeService: GradeService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun consumeGradeMultiplier(): Consumer<Flux<GradeMultiplier>> = Consumer { stream ->
        stream.doOnNext {
            gradeService.emmitMessage(it)
            logger.info("Actual grade-multiplier: $it")
        }.subscribe()
    }
}
