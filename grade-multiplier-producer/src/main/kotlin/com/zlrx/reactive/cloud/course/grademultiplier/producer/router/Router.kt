package com.zlrx.reactive.cloud.course.grademultiplier.producer.router

import com.zlrx.reactive.cloud.course.grademultiplier.producer.model.Multiplier
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class Router(
    private val streamBridge: StreamBridge
) {

    @Bean
    fun routerFn() = router {
        POST("/grade") { request ->
            val body = request.bodyToMono(Multiplier::class.java)
                .map {

                    val result = streamBridge.send(
                        "produceGradeMultiplier-out-0",
                        it
//                        GradeMultiplier(
//                            multiplier = it.value,
//                            createdAt = Instant.now()
//                        )
                    )

                    it.copy(sent = result)
                }
            ServerResponse.ok().body(body, Multiplier::class.java)
        }
    }
}
