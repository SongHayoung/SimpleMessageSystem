plugins {
}

dependencies {
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

    testImplementation("org.springframework.kafka:spring-kafka-test")
}