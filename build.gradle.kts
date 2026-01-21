import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    jacoco
    id("org.springframework.boot") version "3.2.+"
    id("org.owasp.dependencycheck") version "11.0.0"
    id("com.github.rising3.semver") version "0.8.2"
    id("io.spring.dependency-management") version "1.1.+"
	id("org.asciidoctor.convert") version "1.5.8"
    id("org.jetbrains.dokka") version "2.1.0"
    id("com.google.cloud.tools.jib") version "3.4.4"
    id("org.graalvm.buildtools.native") version "0.11.1"
    //id("org.openrewrite.rewrite") version "6.27.0"

    kotlin("jvm") version "2.0.+"
    kotlin("plugin.spring") version "2.0.+"
    kotlin("plugin.jpa") version "2.0.+"
	kotlin("plugin.allopen") version "2.0.+"
    kotlin("plugin.noarg") version "2.0.+"
}

group = "io.dereknelson.lostcities"
version = project.property("version")!!

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

tasks.named<BootRun>("bootRun") {
    if(rootProject.hasProperty("debug")) {
        systemProperty("spring.profiles.active", "local")
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

//rewrite {
//    activeRecipe("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3")
    //exportDatatables = true
//}

val hibernateVersion: String = "6.5.+"
val kotlinLoggingVersion: String = "3.0.+"
val commonsLangVersion: String = "3.18.+"
val jjwtVersion: String = "0.12.7"

dependencies {
    //rewrite("org.openrewrite:rewrite-kotlin:1.21.2")
    //rewrite("org.openrewrite.recipe:rewrite-spring:5.22.0")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    if(  rootProject.hasProperty("debug")){
        implementation(project(":lostcities-common"))
        implementation(project(":lostcities-models"))
    } else {
        implementation("io.dereknelson.lostcities-cloud:lostcities-common:${rootProject.extra["lostcities-common.version"]}")
        implementation("io.dereknelson.lostcities-cloud:lostcities-models:${rootProject.extra["lostcities-models.version"]}")
    }

    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("org.apache.httpcomponents.client5:httpclient5:${rootProject.extra["httpclient5.version"]}")
    implementation("org.apache.httpcomponents.core5:httpcore5:${rootProject.extra["httpcore5.version"]}")

    implementation("org.apache.commons:commons-lang3:${rootProject.extra["commonsLang3.version"]}")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-amqp")


    implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${rootProject.extra["springdoc.version"]}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${rootProject.extra["springdoc.version"]}")

	implementation("io.jsonwebtoken:jjwt-api:${jjwtVersion}")
	implementation("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
	implementation("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")

    implementation("org.hibernate:hibernate-core:${hibernateVersion}")
    implementation("org.hibernate:hibernate-micrometer:${hibernateVersion}")


    runtimeOnly("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.8.1")
    runtimeOnly("org.postgresql:postgresql")


	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	ktlint("com.pinterest:ktlint:0.49.1") {
		attributes {
			attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
		}
	}

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

semver {
    noGitPush = false
}

tasks.withType<KotlinCompile>() {

    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)

        freeCompilerArgs.addAll(listOf(
            "-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn"
        ))
    }
}

tasks.bootBuildImage {
    docker.host = "unix:///run/user/1000/podman/podman.sock"
}

jib {
    from {
        image = "registry://public.ecr.aws/amazoncorretto/amazoncorretto:21.0.8-al2023-headless"
    }

    to {
        image = "ghcr.io/lostcities-cloud/${project.name}:${project.version}"
        tags = mutableSetOf("latest", "${project.version}")

        auth {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }

}

dependencyCheck {
    failBuildOnCVSS = 11f
    failOnError = false
    formats = mutableListOf("JUNIT", "HTML", "JSON")
    data {
        directory = "${rootDir}/owasp"
    }
    //suppressionFiles = ['shared-owasp-suppressions.xml']
    analyzers {
        assemblyEnabled = false
    }
    nvd {
        apiKey = System.getenv("NVD_KEY")
        delay = 16000
    }
}

//  docker inspect -f "{{ .Size }}" bellsoft/liberica-openjdk-alpine:21

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.asciidoctor {
	dependsOn(tasks.test)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}


