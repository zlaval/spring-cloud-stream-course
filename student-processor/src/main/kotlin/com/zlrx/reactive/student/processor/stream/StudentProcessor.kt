package com.zlrx.reactive.student.processor.stream

import com.zlrx.schemas.Student
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

@Configuration
class StudentProcessor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun processStudents(): Function<Flux<Message<Student>>, Mono<Void>> = Function { stream ->
        stream
            .concatMap {
                processStudent(it)
            }.then()
    }

    private fun processStudent(studentMessage: Message<Student>) = mono {
        val student = studentMessage.payload
        logger.info("Student received: $student")

        val ackHeader = studentMessage.headers.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment::class.java)
        ackHeader?.acknowledge()
    }
}
