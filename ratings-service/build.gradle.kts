plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4") // JSON logs
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    // R2DBC + Spring Data
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Flyway for schema migrations
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    // JDBC driver for Flyway
    runtimeOnly("org.postgresql:postgresql")
    // R2DBC driver
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    // Proto
    implementation(project(":common-proto"))
    implementation("io.grpc:grpc-netty-shaded:1.63.0")
    implementation("io.grpc:grpc-stub:1.63.0")
    implementation("io.grpc:grpc-protobuf:1.63.0")
    // ES
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.10.2")
    // Eureka Client
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-webtestclient")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.projectreactor:reactor-test")
    // Testcontainers BOM – one place for TC versions
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.0"))
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
