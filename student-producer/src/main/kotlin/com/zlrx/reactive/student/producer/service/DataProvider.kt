package com.zlrx.reactive.student.producer.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zlrx.schemas.student.Address
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct
import kotlin.random.Random

data class Person(
    val name: String,
    val sex: String
)

data class PersonContainer(
    val names: List<Person>
)

data class AddressRow(
    val address: String,
    val city: String,
    val postalCode: String
)

data class AddressContainer(
    val addresses: List<AddressRow>
)

@Service
class DataProvider(
    private val objectMapper: ObjectMapper,

    @Value("classpath:addresses.json")
    private val addressResource: Resource,

    @Value("classpath:names.json")
    private val nameResource: Resource

) {

    private lateinit var addressContainer: AddressContainer

    private lateinit var personContainer: PersonContainer

    private val random = Random(System.nanoTime())

    @PostConstruct
    private fun init() {
        addressContainer = fromJson(addressResource.file, AddressContainer::class.java)
        personContainer = fromJson(nameResource.file, PersonContainer::class.java)
    }

    fun getAddress(): Address {
        val address = addressContainer.addresses[random.nextInt(0, addressContainer.addresses.size)]

        return Address().apply {
            zip = address.postalCode
            city = address.city
            street = address.address
            houseNumber = random.nextInt(100)
        }
    }

    fun getPerson(): Person {
        val people = personContainer.names
        return people[random.nextInt(0, people.size)]
    }

    fun getSubjects(): List<String> {
        val count = random.nextInt(1, 4)
        val randomSubjects = subjects.shuffled()
        return randomSubjects.subList(0, count)
    }

    private val subjects = listOf(
        "Math", "Science", "History", "Geography", "Chemistry", "Computer Science"
    )

    private fun <T> fromJson(json: File, type: Class<T>): T = objectMapper.readValue(json, type)
}
