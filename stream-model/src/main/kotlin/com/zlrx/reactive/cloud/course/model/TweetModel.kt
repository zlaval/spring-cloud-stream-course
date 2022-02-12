package com.zlrx.reactive.cloud.course.model

import java.time.Instant

enum class TweetType {
    PROGRAMMING, SEASON, HOBBY
}

data class TweetModel(
    val id: String,
    val text: String,
    val createdAt: Instant,
    val author: String,
    val type: TweetType
)
