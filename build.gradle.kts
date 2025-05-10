plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}


group = "com.hd.misale"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("dev.langchain4j:langchain4j-open-ai-spring-boot-starter:1.0.0-beta3")
	implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.0.0-beta3")
	implementation("org.springframework.boot:spring-boot-starter-data-redis:3.4.4")
	implementation("org.springframework.data:spring-data-redis:3.4.5")
	implementation("org.springframework:spring-webflux:7.0.0-M4")
	implementation("io.projectreactor:reactor-core:3.7.5")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.4.5")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.mockito:mockito-core")
	testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
