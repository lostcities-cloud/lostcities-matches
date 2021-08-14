import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.9"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.asciidoctor.convert") version "1.5.8"
	//id("com.gorylenko.gradle-git-properties") version "2.3.1-rc1"

	kotlin("jvm") version "1.5.0-RC"
	kotlin("plugin.spring") version "1.5.0-RC"
	kotlin("plugin.jpa") version "1.5.0-RC"
}

group = "io.dereknelson"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_13

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}
allprojects {
	repositories {
		mavenCentral()
		maven { setUrl("https://jitpack.io") }
	}
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
	//implementation("com.github.nelson54.lostcities-cloud:lostcities-common:master-SNAPSHOT")
	implementation(project(":LostCitiesCommon"))

	implementation("org.apache.commons:commons-lang3:3.12.0")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	//implementation("org.springframework.boot:thymeleaf-extras-springsecurity5")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hppc")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")
	implementation("com.fasterxml.jackson.core:jackson-annotations")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.zalando:problem-spring-web:0.21.0")
	implementation("org.springdoc:springdoc-openapi-webmvc-core:1.5.10")
	implementation("org.springdoc:springdoc-openapi-ui:1.5.10")
	implementation("org.springdoc:springdoc-openapi-kotlin:1.5.10")

	implementation("org.modelmapper:modelmapper:2.4.1")
	implementation("org.flywaydb:flyway-core")

	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

	implementation("org.springframework.kafka:spring-kafka:2.7.2")

	implementation("org.hibernate:hibernate-jcache")
	implementation("org.ehcache.modules:ehcache-107:3.9.2")
	implementation("org.ehcache:ehcache:3.9.2")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("com.h2database:h2")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	
	testImplementation("org.mockito:mockito-core:3.+")
	testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
	testImplementation("org.assertj:assertj-core:3.19.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "15"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks.test {
	outputs.dir(snippetsDir)
}

tasks.asciidoctor {
	inputs.dir(snippetsDir)
	dependsOn(tasks.test)
}
