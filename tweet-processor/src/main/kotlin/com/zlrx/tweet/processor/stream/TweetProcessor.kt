package com.zlrx.tweet.processor.stream

import com.zlrx.reactive.cloud.course.model.TweetMetadata
import com.zlrx.reactive.cloud.course.model.TweetModel
import com.zlrx.tweet.processor.service.TweetService
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Function

@Configuration
class TweetProcessor(
    private val service: TweetService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun processTweets(): Function<Flux<TweetModel>, Flux<TweetMetadata>> = Function { stream ->
        stream.flatMap {
            mono {
                service.processTweet(it)
            }
        }.onErrorContinue { t, u ->
            logger.error(t.message, t)
        }
    }
}
