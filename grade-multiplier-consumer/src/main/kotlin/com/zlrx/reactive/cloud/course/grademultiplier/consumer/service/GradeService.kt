package com.zlrx.reactive.cloud.course.grademultiplier.consumer.service

import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import javax.annotation.PostConstruct

@Service
class GradeService {

    private val processor = Sinks.many().unicast().onBackpressureBuffer<GradeMultiplier>()

    private lateinit var gradeFlux: Flux<GradeMultiplier>

    @PostConstruct
    fun init() {
        gradeFlux = processor.asFlux().publish().autoConnect()
    }

    fun emmitMessage(gradeMultiplier: GradeMultiplier) {
        processor.emitNext(gradeMultiplier, Sinks.EmitFailureHandler.FAIL_FAST)
    }

    fun getStream() = gradeFlux
}
