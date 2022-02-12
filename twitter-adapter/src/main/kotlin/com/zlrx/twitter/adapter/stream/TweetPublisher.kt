package com.zlrx.twitter.adapter.stream

import com.zlrx.reactive.cloud.course.model.TweetModel
import com.zlrx.twitter.adapter.service.TwitterConnector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import reactor.core.publisher.Flux
import java.util.function.Supplier

@Configuration
class TweetPublisher(
    private val connector: TwitterConnector
) {

    @Bean
    fun publishTweets(): Supplier<Flux<Message<TweetModel>>> = Supplier {
        connector.getStream().map {
            MessageBuilder.withPayload(it).setHeader("tweet_type", it.type.name).build()
        }
    }
}
