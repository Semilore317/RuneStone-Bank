plugins {
    java
    id("org.springframework.boot") version "3.5.12"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
//    id("com.diffplug.spotless") version "8.4.0"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.abraham_bankole"
version = "0.0.1-SNAPSHOT"

description = "runestone-bank"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // LOMBOK
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // 3rd Party
    implementation("org.projectlombok:lombok:1.18.34")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("me.paulschwarz:spring-dotenv:5.1.0")
    implementation("com.itextpdf:itextpdf:5.5.13.4")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")

    runtimeOnly("org.postgresql:postgresql:42.7.10")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    // Java formatting rules
    java {
        target("src/main/java/**/*.java", "src/test/java/**/*.java")
        googleJavaFormat("1.17.0") // Google's style guide
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }

    // Kotlin formatting rules
    kotlin {
        target("src/main/kotlin/**/*.kt", "src/test/kotlin/**/*.kt")
        ktlint("0.50.0")
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }

    format("misc") {
        target("**/*.md", "**/.gitignore")
        indentWithSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// Specifying that Kotlin and Java both target JVM 21
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}