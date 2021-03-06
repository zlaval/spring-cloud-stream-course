package com.zlrx.reactive.student.producer.stream

import com.zlrx.reactive.student.producer.service.DataProvider
import com.zlrx.schemas.Student
import com.zlrx.schemas.common.Context
import com.zlrx.schemas.student.Sex
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.function.Supplier
import kotlin.random.Random

@Configuration
class StudentProvider(
    private val dataProvider: DataProvider
) {

    private val random = Random(System.nanoTime())

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun produceStudents(): Supplier<Flux<Message<Student>>> = Supplier {
        Flux.interval(Duration.ofMillis(random.nextLong(100)))
            .map {
                generateStudent(it % 10)
            }.doOnNext {
                logger.info("Student sent: $it")
            }
    }

    private fun generateStudent(index: Long): Message<Student> {
        val student = dataProvider.getPerson()
        val studentAvro = Student().apply {
            context = Context().apply {
                id = UUID.randomUUID().toString()
                producer = "Student Producer App"
                timestamp = Instant.now()
            }

            id = index
            name = student.name
            birth = random.nextInt(1980, 2015)
            gpa = random.nextDouble(1.0, 5.0).toFloat()
            active = random.nextBoolean()
            sex = if (student.sex == "MALE") Sex.MALE else Sex.FEMALE
            subject = dataProvider.getSubjects()
            address = dataProvider.getAddress()
        }

        val ageGroup = (studentAvro.birth / 10) * 10

        return MessageBuilder.withPayload(studentAvro)
            .setHeader("age-group", ageGroup)
            .setHeader(KafkaHeaders.MESSAGE_KEY, ageGroup.toString())
            .build()
    }
}
