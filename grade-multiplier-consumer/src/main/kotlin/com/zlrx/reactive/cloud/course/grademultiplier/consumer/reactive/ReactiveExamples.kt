package com.zlrx.reactive.cloud.course.grademultiplier.consumer.reactive

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.random.Random

fun main() {
    val examples = ReactiveExamples()
    examples.example()
}

class ReactiveExamples {

    fun example() {
        val vehicleTypes = setOf("Toyota", "Audi", "Tesla", "Ford", "Ferrari", "Mercedes", "Hyundai")

        val start = System.currentTimeMillis()
        Flux.fromIterable(vehicleTypes)
            .flatMap { MockWebService.getVehicle(it) }
            .map { Car(it.type, it.vin) }
            .flatMap { MockDatabase.saveCar(it) }
            .doOnComplete {
                val time = System.currentTimeMillis() - start
                println("Time taken: $time")
            }
            .subscribe { println("Processing finished: $it") }

        Thread.sleep(15000)
    }

    companion object {
        val random = Random(System.nanoTime())
    }

    data class Vehicle(
        val type: String,
        val vin: Int = random.nextInt(1000, 9999)
    )

    data class Car(
        val type: String,
        val vin: Int,
        val id: Int? = null
    )

    object MockWebService {
        fun getVehicle(type: String) = Mono.just(Vehicle(type))
            .delayElement(Duration.ofMillis(random.nextLong(500, 900)))
            .doOnNext { println("Webservice Response: $it") }
    }

    object MockDatabase {
        fun saveCar(car: Car) = Mono.just(
            car.copy(id = random.nextInt(1, 999))
        ).delayElement(Duration.ofMillis(random.nextLong(100, 500)))
            .doOnNext { println("Database save successful: $it") }
    }
}
