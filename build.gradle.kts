import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.8"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.asciidoctor.convert") version "1.5.8"
	//id("com.google.cloud.tools.jib") version "3.1.4"
	//id("com.gorylenko.gradle-git-properties") version "2.3.1-rc1"

    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
	kotlin("plugin.allopen") version "1.4.32"
}

group = "io.dereknelson.lostcities"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_16

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	maven {
		url = uri("https://maven.pkg.github.com/lostcities-cloud/lostcities-common")
		credentials {
			username = System.getenv("GITHUB_ACTOR")
			password = System.getenv("GITHUB_TOKEN")
		}
	}

	maven {
		url = uri("https://maven.pkg.github.com/lostcities-cloud/lostcities-models")
		credentials {
			username = System.getenv("GITHUB_ACTOR")
			password = System.getenv("GITHUB_TOKEN")
		}
	}
	mavenCentral()
}


allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

extra["snippetsDir"] = file("build/generated-snippets")

val ktlint by configurations.creating

dependencies {
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

	implementation("io.dereknelson.lostcities-cloud:lostcities-common:1.0-SNAPSHOT")
	implementation("io.dereknelson.lostcities-cloud:lostcities-models:1.0-SNAPSHOT")

    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")

	implementation("org.apache.commons:commons-lang3:3.12.0")

	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude(module="spring-boot-starter-tomcat")
	}
	implementation("org.springframework.boot:spring-boot-starter-undertow")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("com.google.cloud:spring-cloud-gcp-starter:2.0.8")
    implementation("com.google.cloud:spring-cloud-gcp-starter-secretmanager:2.0.8")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hppc")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")

	implementation("org.zalando:problem-spring-web:0.21.0")
	implementation("org.springdoc:springdoc-openapi-ui:1.5.10")
	implementation("org.springdoc:springdoc-openapi-kotlin:1.5.10")

	implementation("org.flywaydb:flyway-core")

	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

	implementation("org.hibernate:hibernate-jcache")
	implementation("org.ehcache.modules:ehcache-107:3.9.2")
	implementation("org.ehcache:ehcache:3.9.2")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	runtimeOnly("org.postgresql:postgresql:42.2.14")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	ktlint("com.pinterest:ktlint:0.44.0") {
		attributes {
			attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
		}
	}

	testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")

	testImplementation("org.assertj:assertj-core:3.22.0")
}

tasks.bootRun {
	if (project.hasProperty("debug_jvm")) {
		jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5915")
	}
}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
	inputs.files(inputFiles)
	outputs.dir(outputDir)

	description = "Check Kotlin code style."
	classpath = ktlint
	mainClass.set("com.pinterest.ktlint.Main")
	args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
	inputs.files(inputFiles)
	outputs.dir(outputDir)
	description = "Fix Kotlin code style deviations."
	classpath = ktlint
	mainClass.set("com.pinterest.ktlint.Main")
	args = listOf("-F", "src/**/*.kt")
	jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "16"
	}
}

tasks.getByName<BootBuildImage>("bootBuildImage") {
    imageName = "ghcr.io/lostcities-cloud/${project.name}:$version"
    isPublish = true
    environment = mapOf(
        "BP_JVM_VERSION" to "17.*",
        "BPL_DEBUG_ENABLED" to "true"
    )
    builder = "paketobuildpacks/builder:base"
    buildpacks = listOf(
        "gcr.io/paketo-buildpacks/eclipse-openj9",
        "paketo-buildpacks/java",
        "gcr.io/paketo-buildpacks/spring-boot"
    )

    docker {
        publishRegistry {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
            email = "lostcities@dereknelson.io"
        }
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
