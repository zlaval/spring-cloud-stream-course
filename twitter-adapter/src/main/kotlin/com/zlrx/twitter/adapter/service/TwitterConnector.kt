package com.zlrx.twitter.adapter.service

import com.zlrx.reactive.cloud.course.model.TweetModel
import com.zlrx.reactive.cloud.course.model.TweetType
import com.zlrx.twitter.adapter.configuration.TwitterConfiguration
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.dto.tweet.Tweet
import io.github.redouane59.twitter.signature.TwitterCredentials
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.time.ZoneOffset
import javax.annotation.PostConstruct

@Service
class TwitterConnector(
    private val configuration: TwitterConfiguration
) {

    private lateinit var client: TwitterClient

    private val logger = LoggerFactory.getLogger(javaClass)

    private val stream = Sinks.many().unicast().onBackpressureBuffer<TweetModel>()

    private val filters = mapOf(
        "kotlin" to TweetType.PROGRAMMING,
        "kafka" to TweetType.PROGRAMMING,
        "reactjs" to TweetType.PROGRAMMING,
        "nodejs" to TweetType.PROGRAMMING,

        "summer" to TweetType.SEASON,
        "spring" to TweetType.SEASON,
        "autumn" to TweetType.SEASON,
        "winter" to TweetType.SEASON,

        "hiking" to TweetType.HOBBY,
        "baseball" to TweetType.HOBBY,
        "paintball" to TweetType.HOBBY,
        "basketball" to TweetType.HOBBY,
    )

    @PostConstruct
    fun init() {
        client = TwitterClient(
            TwitterCredentials.builder()
                .accessToken(configuration.accessToken)
                .accessTokenSecret(configuration.accessTokenSecret)
                .apiKey(configuration.apiKey)
                .apiSecretKey(configuration.apiSecret)
                .build()
        )
        manageRules()
        startStream()
    }

    private fun startStream() {
        client.startFilteredStream {
            val tweet = mapTweet(it)
            logger.info("Emmit tweet: $tweet")
            stream.emitNext(tweet, Sinks.EmitFailureHandler.FAIL_FAST)
        }
    }

    private fun mapTweet(tweet: Tweet) = TweetModel(
        id = tweet.id,
        text = tweet.text,
        createdAt = tweet.createdAt.toInstant(ZoneOffset.UTC),
        author = tweet.user.name,
        type = mapTweetType(tweet)
    )

    private fun mapTweetType(tweet: Tweet): TweetType {
        return tweet.matchingRules
            .map { it.tag }
            .map { TweetType.valueOf(it) }
            .first()
    }

    private fun manageRules() {
        filters.forEach { (name, type) ->
            client.deleteFilteredStreamRule(name)
            client.addFilteredStreamRule(name, type.name)
        }
    }

    fun getStream() = stream.asFlux()
}
