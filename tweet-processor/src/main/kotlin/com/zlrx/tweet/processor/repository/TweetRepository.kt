package com.zlrx.tweet.processor.repository

import com.zlrx.reactive.cloud.course.model.TweetModel
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TweetRepository : CoroutineCrudRepository<TweetModel, String>
