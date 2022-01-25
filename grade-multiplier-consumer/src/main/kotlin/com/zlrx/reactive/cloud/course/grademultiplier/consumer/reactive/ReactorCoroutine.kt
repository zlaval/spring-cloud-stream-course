package com.zlrx.reactive.cloud.course.grademultiplier.consumer.reactive

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

fun main() {
    val example = ReactorCoroutine()
    example.run()
}

object LicenceService {
    fun getLicencePlateNumber() = Mono.just("ZLRX-777").delayElement(Duration.ofMillis(200))
}

object VrcService {
    suspend fun getVrc(): Int {
        delay(150)
        return 123456
    }
}

data class Car(
    val type: String,
    val licencePlate: String,
    val vrc: Int
)

class ReactorCoroutine {

    fun run() {
        Flux.just("BMW", "Fiat", "Hyundai")
            .flatMap {
                createCar(it)
            }.switchIfEmpty(Mono.just(Car("Tesla", "SUO012", 234552)))
            .retry(2)
            .subscribe {
                println(it)
            }

        Thread.sleep(5000)
    }

    private fun createCar(type: String) = mono {
        val licencePlate = LicenceService.getLicencePlateNumber().awaitSingle()
        val vrc = VrcService.getVrc()
        Car(type, licencePlate, vrc)
    }
}
