package com.zlrx.reactive.student.processor.stream

import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import com.zlrx.reactive.student.processor.model.Person
import com.zlrx.schemas.Student
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import java.time.LocalDate
import java.util.function.Function
import javax.annotation.PostConstruct

@Configuration
class StudentProcessor(
    metricRegistry: MeterRegistry
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val childrenCounter = Counter.builder("children-counter")
        .tag("child.student.counter", "age.under.eighteen")
        .register(metricRegistry)

    @PostConstruct
    fun init() {
        Schedulers.enableMetrics()
    }

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

        studentStream
            .subscribeOn(Schedulers.newBoundedElastic(100, Integer.MAX_VALUE, "ZLRX_THREAD"))
            .map {
                it.payload
            }.withLatestFrom(gradeMultiplierStream) { s, g ->
                Person(
                    id = s.id,
                    name = s.name,
                    birth = s.birth,
                    finalGrade = s.gpa * g.multiplier
                )
            }.doOnNext {
                val age = LocalDate.now().minusYears(it.birth.toLong()).year
                if (age <= 18) {
                    childrenCounter.increment()
                }
            }
    }
}
