package com.zlrx.twitter.rest.router

import com.zlrx.twitter.rest.model.TweetAggregateResponse
import com.zlrx.twitter.rest.service.TweetMetadataAggregator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class ChartRouter(
    private val service: TweetMetadataAggregator
) {

    @Bean
    fun routerFn() = router {
        "/api/v1".nest {
            GET("/chart") {
                val resultStream = service.getStream()
                ServerResponse
                    .ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(resultStream, TweetAggregateResponse::class.java)
            }
        }
    }
}
