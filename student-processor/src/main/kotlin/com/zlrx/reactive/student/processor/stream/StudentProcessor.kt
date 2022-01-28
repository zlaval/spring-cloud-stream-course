package com.zlrx.reactive.student.processor.stream

import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import com.zlrx.reactive.student.processor.model.Person
import com.zlrx.schemas.Student
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import java.time.Duration
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Function

@Configuration
class StudentProcessor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun produceNameAndCity(): Function<Flux<Student>, Tuple2<Flux<String>, Flux<String>>> = Function { stream ->
        val underThirtyStream = stream.filter {
            logger.info("Filter ${it.name}")
            LocalDate.now().year - it.birth < 30
        }.publish().autoConnect(2)

        val nameStream = underThirtyStream
            .doOnNext { logger.info("Publish name: ${it.name}") }
            .map { it.name }

        val cityStream = underThirtyStream
            .doOnNext { logger.info("Publish city: ${it.address.city}") }
            .map { it.address.city }

        Tuples.of(nameStream, cityStream)
    }

    @Bean
    fun processStudents(): Function<Tuple2<Flux<Message<Student>>, Flux<GradeMultiplier>>, Flux<Person>> = Function { stream ->
        val studentStream = stream.t1
        val gradeMultiplierStream = stream.t2

        studentStream.map {
            it.payload
        }.withLatestFrom(gradeMultiplierStream) { s, g ->
            Person(
                id = s.id,
                name = s.name,
                birth = s.birth,
                finalGrade = s.gpa * g.multiplier
            )
        }.concatMap {
            Mono.delay(Duration.ofMillis(ThreadLocalRandom.current().nextLong(50, 1500)))
                .then(Mono.just(it))
        }
    }
}
