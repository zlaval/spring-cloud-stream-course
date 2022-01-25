package com.zlrx.reactive.student.producer.configuration

import org.apache.avro.data.TimeConversions
import org.apache.avro.specific.SpecificData
import org.springframework.cloud.function.context.converter.avro.AvroSchemaMessageConverter
import org.springframework.cloud.function.context.converter.avro.AvroSchemaServiceManagerImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeType
import javax.annotation.PostConstruct

@Configuration
class AvroConfiguration {

    @PostConstruct
    fun configureTimestampConversion() {
        SpecificData.get().addLogicalTypeConversion(TimeConversions.TimestampMillisConversion())
    }

    @Bean
    fun avroSchemaMessageConverter(): AvroSchemaMessageConverter = AvroSchemaMessageConverter(
        MimeType("application", "*+avro"), AvroSchemaServiceManagerImpl()
    )
}
