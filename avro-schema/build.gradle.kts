import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask

apply(plugin = "com.github.davidmc24.gradle.plugin.avro")

dependencies {
    implementation("io.confluent:kafka-avro-serializer:6.2.2")
    implementation("org.apache.avro:avro:1.11.0")
}

val avro = tasks.named<GenerateAvroJavaTask>("generateAvroJava").get()
avro.setFieldVisibility("PRIVATE")

tasks.bootJar {
    enabled = false
}