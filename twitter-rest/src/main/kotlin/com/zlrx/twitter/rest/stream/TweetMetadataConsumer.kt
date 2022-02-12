package com.zlrx.twitter.rest.stream

import com.zlrx.reactive.cloud.course.model.TweetMetadata
import com.zlrx.twitter.rest.service.TweetMetadataAggregator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.function.Consumer

@Configuration
class TweetMetadataConsumer(
    private val service: TweetMetadataAggregator
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun consumeMetadata(): Consumer<Flux<TweetMetadata>> = Consumer { stream ->

        stream.window(Duration.ofSeconds(3))
            .flatMap {
                mono {
                    val list = it.collectList().awaitSingle()
                    service.aggregate(list)
                }
            }
            .onErrorContinue { t, u -> logger.error(t.message) }
            .subscribe()
    }
}
