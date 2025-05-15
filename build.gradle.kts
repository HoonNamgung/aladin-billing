plugins {
	kotlin("jvm") version "1.9.0"
	kotlin("plugin.spring") version "1.9.0"
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// SQLite
	runtimeOnly("org.xerial:sqlite-jdbc:3.42.0.0")
	implementation("org.hibernate.orm:hibernate-community-dialects:6.2.5.Final")

	// Test
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("io.mockk:mockk:1.13.9") // 최신 버전 기준
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.mockito") // mockito 제거 (optional)
	}
	testImplementation("org.mockito:mockito-core:4.11.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0") // 👈 이게 반드시 필요!
	testImplementation(kotlin("test"))
}
