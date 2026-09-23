import org.graalvm.buildtools.gradle.dsl.GraalVMExtension

plugins {
  java
  id("org.springframework.boot") version "3.5.12"
  id("io.spring.dependency-management") version "1.1.7"

  kotlin("jvm") version "2.1.0"
  kotlin("plugin.spring") version "2.1.0"
  kotlin("plugin.jpa") version "2.1.0"

  id("com.diffplug.spotless") version "7.0.2"

  // Resolve the GraalVM plugin, but don't apply it to normal JVM builds.
  // Applying it causes Spring Boot to wire AOT tasks into bootJar.
  id("org.graalvm.buildtools.native") version "0.10.1" apply false
}

group = "com.abraham_bankole"

version = "0.0.1-SNAPSHOT"

description = "runestone-bank"

val springdocVersion = "2.8.5"
val jjwtVersion = "0.11.5"

// Native builds can be requested either explicitly with -Pnative
// or by invoking a native* Gradle task such as nativeCompile.
//
// This keeps normal bootJar/dev builds JVM-only while preserving the
// existing CI command: ./gradlew nativeCompile
val nativeBuildRequested =
  providers.gradleProperty("native").isPresent ||
          gradle.startParameter.taskNames.any { taskName ->
            taskName.substringAfterLast(":").startsWith("native")
          }

if (nativeBuildRequested) {
  apply(plugin = "org.graalvm.buildtools.native")

  extensions.configure<GraalVMExtension> {
    binaries {
      named("main") {
        imageName.set("runestone-bank")
        mainClass.set(
          "com.abraham_bankole.runestone_bank.RuneStoneBankApplication"
        )
      }
    }
  }
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.kafka:spring-kafka")

  implementation("io.jsonwebtoken:jjwt-api:${jjwtVersion}")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")

  runtimeOnly("org.postgresql:postgresql")

  implementation(
    "org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdocVersion}"
  )

  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  compileOnly("org.projectlombok:lombok:1.18.34")
  annotationProcessor("org.projectlombok:lombok:1.18.34")
  testCompileOnly("org.projectlombok:lombok:1.18.34")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

  implementation("me.paulschwarz:spring-dotenv:5.1.0")
  implementation("com.itextpdf:itextpdf:5.5.13.4")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test")

  testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

  // Testcontainers (integration tests only)
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.testcontainers:kafka")

  // H2 for @DataJpaTest
  testRuntimeOnly("com.h2database:h2")
}

// Task Configurations

tasks.withType<Test> {
  useJUnitPlatform()
}

// Kotlin and Java both target JVM 21
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
  }
}

spotless {
  java {
    target("src/**/*.java")
    importOrder()
    removeUnusedImports()
    googleJavaFormat("1.17.0")
    trimTrailingWhitespace()
    endWithNewline()
  }

  kotlin {
    ktfmt("0.53").googleStyle()
    trimTrailingWhitespace()
    endWithNewline()
  }

  kotlinGradle {
    target("*.gradle.kts")
    ktfmt("0.53").googleStyle()
  }

  format("misc") {
    target("**/*.md", "**/.gitignore", "**/*.yml")
    leadingTabsToSpaces()
    trimTrailingWhitespace()
    endWithNewline()
  }
}