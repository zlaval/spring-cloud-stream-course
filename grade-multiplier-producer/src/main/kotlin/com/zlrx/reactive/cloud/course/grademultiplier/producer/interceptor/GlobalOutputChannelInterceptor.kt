package com.zlrx.reactive.cloud.course.grademultiplier.producer.interceptor

import com.zlrx.reactive.cloud.course.model.GradeMultiplier
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor

// @Component
// @GlobalChannelInterceptor(patterns = ["*-out-*"])
class GlobalOutputChannelInterceptor : ChannelInterceptor {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun afterSendCompletion(message: Message<*>, channel: MessageChannel, sent: Boolean, ex: Exception?) {
        super.afterSendCompletion(message, channel, sent, ex)
        logger.info("Message was sent to $channel. $message")
    }

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val payload = message.payload
        return if (payload is GradeMultiplier && payload.multiplier > 30) {
            super.preSend(message, channel)
        } else {
            null
        }
    }
}
