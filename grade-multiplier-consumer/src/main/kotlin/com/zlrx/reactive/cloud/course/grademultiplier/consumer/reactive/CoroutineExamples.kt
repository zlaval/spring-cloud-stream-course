package com.zlrx.reactive.cloud.course.grademultiplier.consumer.reactive

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() {
    val example = CoroutineExamples()
    example.run()
}

class CoroutineExamples {

    fun run() = runBlocking {
        val vehicleTypes = setOf("Toyota", "Audi", "Tesla", "Ford", "Ferrari", "Mercedes", "Hyundai")

        val start = System.currentTimeMillis()

        val vehiclesAsync = vehicleTypes.map {
            async { MockWebService.getVehicle(it) }
        }

        val persistedCarsAsync = vehiclesAsync.map {
            async {
                val vehicle = it.await()
                val car = Car(vehicle.type, vehicle.vin)
                MockDatabase.saveVehicle(car)
            }
        }

        val result = persistedCarsAsync.awaitAll()

        val time = System.currentTimeMillis() - start
        println("Time taken: $time")
        println(result)
    }

    object MockWebService {
        suspend fun getVehicle(type: String): Vehicle {
            delay(random.nextLong(500, 900))
            val vehicle = Vehicle(type)
            println("Webservice request: $vehicle")
            return vehicle
        }
    }

    object MockDatabase {
        suspend fun saveVehicle(car: Car): Car {
            delay(random.nextLong(100, 150))
            val persistedCar = car.copy(id = random.nextInt(1, 999))
            println("Save successful $car")
            return persistedCar
        }
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
}
