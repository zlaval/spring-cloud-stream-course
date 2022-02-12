package com.zlrx.twitter.rest.service

import com.zlrx.reactive.cloud.course.model.TweetMetadata
import com.zlrx.reactive.cloud.course.model.TweetType
import com.zlrx.twitter.rest.model.TweetAggregateResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks

@Service
class TweetMetadataAggregator {

    private val stream = Sinks.many().multicast().directBestEffort<List<TweetAggregateResponse>>()

    private val logger = LoggerFactory.getLogger(javaClass)

    private val defaultData = TweetType.values().map { TweetMetadata(it, 0) }

    fun aggregate(data: List<TweetMetadata>) {
        val response = (defaultData + data).groupBy {
            it.type
        }.map { (name, value) ->
            val sum = value.sumOf { it.letterCount }
            TweetAggregateResponse(name.name, sum)
        }.sortedBy {
            it.name
        }

        logger.info("Response data: $response")
        stream.emitNext(response, Sinks.EmitFailureHandler.FAIL_FAST)
    }

    fun getStream() = stream.asFlux()
}
