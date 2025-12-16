import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.math.BigDecimal
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
	kotlin("jvm") version "2.2.21" apply false
	kotlin("plugin.spring") version "2.2.21" apply false
	id("org.springframework.boot") version "4.0.0" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false

    id("org.jlleitschuh.gradle.ktlint") version "14.0.1" apply false

    jacoco

    id("org.sonarqube") version "5.1.0.4882"
}

allprojects {
    group = "com.streamalyze"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "Teapotka_streamalyze")
        property("sonar.organization", "teapotka")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL"))
        property("sonar.login", System.getenv("SONAR_LOGIN"))
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.jacoco.reportPaths", "build/jacoco/test.exec")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                "-Xjsr305=strict",
                "-Xannotation-default-target=param-property"
            )
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
            showStandardStreams = true
        }
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")

    tasks.named("ktlintCheck") {}

    tasks.withType<JacocoReport>().configureEach {
        dependsOn(tasks.withType<Test>())
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

      plugins.withId("jacoco") {
        // Configure ALL JacocoCoverageVerification tasks created by the plugin
        tasks.withType<JacocoCoverageVerification>().configureEach {
            violationRules {
                rule {
                    limit {
                        minimum = BigDecimal("0.6") // 60% minimum coverage
                    }
                }
            }
        }
      }


     tasks.matching { it.name == "check" }.configureEach  {
        dependsOn(
            "ktlintCheck",
            // "detekt",
            "jacocoTestReport",
            "jacocoTestCoverageVerification"
        )
    }

     configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.0")
        }
    }

}
