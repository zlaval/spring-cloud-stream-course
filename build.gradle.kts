import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.10"
    id("maven-publish")
    id("org.jmailen.kotlinter") version "3.8.0"
    id("com.adarshr.test-logger") version "3.1.0"
    id("org.springframework.boot") version "2.6.2" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

allprojects {
    apply(plugin = "org.jmailen.kotlinter")
    apply(plugin = "com.adarshr.test-logger")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")

    group = "com.zlrx.reactive.cloud.course"
    version = "1.0.0"
    java.sourceCompatibility = JavaVersion.VERSION_17

    repositories {
        mavenCentral()
        mavenLocal()
    }

}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.springframework.boot")

    publishing {
        repositories {
            mavenLocal()
        }
        publications {
            register("mavenJava", MavenPublication::class.java) {
                from(components["java"])
            }
        }
    }

    kotlinter {
        ignoreFailures = true
        disabledRules = arrayOf("import-ordering", "filename")
    }

    extra["springCloudVersion"] = "2021.0.0"

    configurations.forEach {
        it.exclude(module = "spring-boot-tomcat")
        it.exclude(module = "mockito-core")
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.springframework.cloud:spring-cloud-stream")
        implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
        implementation("io.projectreactor.netty:reactor-netty:1.0.14")

        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

        testImplementation("org.springframework.boot:spring-boot-starter-test")

    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.compileKotlin { dependsOn(tasks.lintKotlin) }
    tasks.lintKotlin { dependsOn(tasks.formatKotlin) }

}