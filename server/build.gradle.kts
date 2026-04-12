plugins{
    java
    id("org.springframework.boot") version "3.5.12"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
}

group = 'com.abraham_bankole'
version = '0.0.1-SNAPSHOT'

description = """runestone-bank"""

sourceCompatibility = 1.5
targetCompatibility = 1.5
tasks.withType(JavaCompile) {
	options.encoding = 'UTF-8'
}



repositories {
        
     maven { url "https://repo.maven.apache.org/maven2" }
}
dependencies {
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-data-jpa', version:'3.5.12'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-web', version:'3.5.12'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-security', version:'3.5.12'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-mail', version:'3.5.12'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-actuator', version:'3.5.12'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-validation', version:'3.5.12'
    compile group: 'org.jetbrains.kotlin', name: 'kotlin-stdlib', version:'2.1.0'
    compile group: 'org.jetbrains.kotlin', name: 'kotlin-reflect', version:'2.1.0'
    compile group: 'org.projectlombok', name: 'lombok', version:'1.18.44'
    compile group: 'org.springdoc', name: 'springdoc-openapi-starter-webmvc-ui', version:'2.8.5'
    compile group: 'me.paulschwarz', name: 'spring-dotenv', version:'5.1.0'
    compile group: 'com.itextpdf', name: 'itextpdf', version:'5.5.13.4'
    compile group: 'io.jsonwebtoken', name: 'jjwt-api', version:'0.11.5'
    compile group: 'org.springframework.kafka', name: 'spring-kafka', version:'3.3.14'
    runtime group: 'org.postgresql', name: 'postgresql', version:'42.7.10'
    runtime group: 'io.jsonwebtoken', name: 'jjwt-impl', version:'0.11.5'
    runtime group: 'io.jsonwebtoken', name: 'jjwt-jackson', version:'0.11.5'
    testCompile group: 'org.springframework.boot', name: 'spring-boot-starter-test', version:'3.5.12'
    testCompile group: 'org.jetbrains.kotlin', name: 'kotlin-test', version:'2.1.0'
}
