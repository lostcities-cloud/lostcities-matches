import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.+"
    // id("org.graalvm.buildtools.native") version "0.10.2"
    id("io.spring.dependency-management") version "1.1.+"
	id("org.asciidoctor.convert") version "1.5.8"
    id("org.jetbrains.dokka") version "1.6.10"
    id("com.google.cloud.tools.jib") version "3.4.2"
	//id("com.gorylenko.gradle-git-properties") version "2.3.1-rc1"

    kotlin("jvm") version "2.0.+"
    kotlin("plugin.spring") version "2.0.+"
    kotlin("plugin.jpa") version "2.0.+"
	kotlin("plugin.allopen") version "2.0.+"
    kotlin("plugin.noarg") version "2.0.+"
}

group = "io.dereknelson.lostcities"
version = "0.0.1-SNAPSHOT"




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
    mavenCentral()
}


allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
	annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

extra["snippetsDir"] = file("build/generated-snippets")

val ktlint by configurations.creating


dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-registry-elastic:latest.release")
    implementation(project(":lostcities-common"))
    implementation(project(":lostcities-models"))

    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")

	implementation("org.apache.commons:commons-lang3:3.12.0")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("io.micrometer:micrometer-registry-otlp")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hppc")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.8.0")

	implementation("org.flywaydb:flyway-core")

	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

    implementation("org.hibernate:hibernate-core:6.4.4.Final")
    implementation("org.hibernate:hibernate-micrometer:6.4.4.Final")
    implementation("org.hibernate:hibernate-jcache:6.4.4.Final")
    implementation("org.ehcache.modules:ehcache-107:3.9.2")
    implementation("org.ehcache:ehcache:3.9.2")
    runtimeOnly("org.flywaydb:flyway-core:10.8.1")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.8.1")
    runtimeOnly("org.postgresql:postgresql:42.3.8")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	ktlint("com.pinterest:ktlint:0.44.0") {
		attributes {
			attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
		}
	}
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10")

    testImplementation("org.assertj:assertj-core:3.22.0")
	testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
}

val outputDir = "${project.layout.buildDirectory}/reports/ktlint/"
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

tasks.withType<KotlinCompile>() {

    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)

        freeCompilerArgs.addAll(listOf(
            "-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn"
        ))
    }
}

jib {
    from {
        image = "registry://bellsoft/liberica-openjdk-alpine:21.0.4-9-cds"
    }
    to {
        image = "ghcr.io/lostcities-cloud/${project.name}:latest"
        auth {
            username = System.getenv("GH_ACTOR")
            password = System.getenv("GH_TOKEN")
        }
    }
}

//  docker inspect -f "{{ .Size }}" bellsoft/liberica-openjdk-alpine:21

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.asciidoctor {
	dependsOn(tasks.test)
}
