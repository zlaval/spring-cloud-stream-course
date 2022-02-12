package com.zlrx.tweet.processor.service

import com.zlrx.reactive.cloud.course.model.TweetMetadata
import com.zlrx.reactive.cloud.course.model.TweetModel
import com.zlrx.tweet.processor.repository.TweetRepository
import org.springframework.stereotype.Service

@Service
class TweetService(
    private val repository: TweetRepository
) {

    suspend fun processTweet(tweetModel: TweetModel): TweetMetadata {
        val tweet = repository.save(tweetModel)
        val type = tweet.type
        val letterCount = tweet.text.length
        return TweetMetadata(type, letterCount)
    }
}
