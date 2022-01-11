package com.zlrx.reactive.cloud.course.model

import java.time.Instant
import java.util.UUID

data class GradeMultiplier(
    val id: String = UUID.randomUUID().toString(),
    val multiplier: Double,
    val createdAt: Instant
)
