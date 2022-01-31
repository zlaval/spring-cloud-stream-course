package com.zlrx.reactive.cloud.course.grademultiplier.consumer.callbackhell

import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom

fun main() {
    // withCallbacks()
    withReactor()
    Thread.sleep(1000)
    UIUtil.shutdown()
}

fun withReactor() {
    val favouriteService = FavouriteService()
    val suggestionService = SuggestionService()

    favouriteService.getFavourites(1)
        .flatMap {
            favouriteService.getDetails(it)
        }.switchIfEmpty {
            suggestionService.getSuggestion()
        }.take(5)
        .publishOn(UIUtil.uiThreadScheduler())
        .doOnError {
            println(it)
        }.subscribe {
            UIUtil.show(it)
        }
}

fun withCallbacks() {
    val favouriteService = FavouriteService()
    val suggestionService = SuggestionService()
    favouriteService.getFavourites(
        777,
        object : Callback<List<String>> {
            override fun onSuccess(values: List<String>) {
                if (values.isEmpty()) {
                    suggestionService.getSuggestion(object : Callback<List<String>> {
                        override fun onSuccess(values: List<String>) {
                            UIUtil.submitOnUIThread {
                                values.stream().limit(5).forEach { UIUtil.show(it) }
                            }
                        }

                        override fun onError(error: Throwable) {
                            println(error)
                        }
                    })
                } else {
                    values.stream().limit(5)
                        .forEach {
                            favouriteService.getDetails(
                                it,
                                object : Callback<String> {
                                    override fun onSuccess(values: String) {
                                        UIUtil.submitOnUIThread {
                                            UIUtil.show(values)
                                        }
                                    }

                                    override fun onError(error: Throwable) {
                                        println(error)
                                    }
                                }
                            )
                        }
                }
            }

            override fun onError(error: Throwable) {
                println(error)
            }
        }
    )
}

object UIUtil {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val executor = Executors.newFixedThreadPool(5)

    fun submitOnUIThread(action: () -> Unit) {
        executor.submit(action)
    }

    fun uiThreadScheduler() = Schedulers.fromExecutor(executor)

    fun show(value: String) {
        logger.info(value)
    }

    fun shutdown() {
        executor.shutdownNow()
    }
}

interface Callback<T> {

    fun onSuccess(values: T)
    fun onError(error: Throwable)
}

class SuggestionService {

    fun getSuggestion(callback: Callback<List<String>>) {
        val data = getDataFromNetwork()
        if (data != null) {
            callback.onSuccess(data)
        } else {
            callback.onError(RuntimeException("Null array"))
        }
    }

    fun getSuggestion() = Flux.just("Matrix", "No country for old men", "Batman").delayElements(Duration.ofMillis(110))

    private fun getDataFromNetwork(): List<String> {
        Thread.sleep(330)
        return listOf("Matrix", "No country for old men", "Batman")
    }
}

class FavouriteService {

    fun getFavourites(userId: Int, callback: Callback<List<String>>) {
        val data = getDataFromNetwork(userId)
        if (data != null) {
            callback.onSuccess(data)
        } else {
            callback.onError(RuntimeException("Null array"))
        }
    }

    fun getFavourites(userId: Int): Flux<String> {
        val empty = ThreadLocalRandom.current().nextBoolean()
        return if (empty) {
            Flux.empty<String>()
        } else {
            Flux.just("LotR", "Superman", "Star Wars").delayElements(Duration.ofMillis(100))
        }
    }

    private fun getDataFromNetwork(userId: Int): List<String> {
        Thread.sleep(300)
        val empty = ThreadLocalRandom.current().nextBoolean()
        return if (empty) listOf("LotR", "Superman", "Star Wars") else emptyList()
    }

    fun getDetails(title: String, callback: Callback<String>) {
        Thread.sleep(110)
        callback.onSuccess("$title - detail")
    }

    fun getDetails(title: String): Mono<String> = Mono.just("$title - detail").delayElement(Duration.ofMillis(110))
}
