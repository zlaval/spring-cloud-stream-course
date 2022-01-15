package com.zlrx.reactive.cloud.course.grademultiplier.consumer.router

import com.zlrx.reactive.cloud.course.grademultiplier.consumer.service.GradeService
import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class GradeRouter(
    private val gradeService: GradeService
) {

    @Bean
    fun routerFn() = router {
        "/api/v1/grade".nest {
            GET("") {
                val messageStream = gradeService.getStream()
                ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(messageStream, GradeMultiplier::class.java)
            }
        }
    }
}
