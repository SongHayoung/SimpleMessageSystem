plugins {
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

dependencies {
    api("org.springframework.data:spring-data-redis:3.1.1")
    api("org.springframework.boot:spring-boot-starter-data-cassandra:3.1.1")
    implementation("io.lettuce:lettuce-core:6.2.4.RELEASE")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.named("compileJava") {
    inputs.files(tasks.named("processResources"))
}
tasks.findByName("bootJar")?.apply {
    enabled = false
}

tasks.findByName("jar")?.apply {
    enabled = true
}