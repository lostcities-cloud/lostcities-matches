package io.dereknelson.lostcities.library

class SpringProfileConstants {
    companion object {
        const val SPRING_PROFILE_DEVELOPMENT: String = "dev"
        const val SPRING_PROFILE_TEST: String = "test"
        const val SPRING_PROFILE_PRODUCTION: String = "prod"
        const val SPRING_PROFILE_CLOUD: String = "cloud"
        const val SPRING_PROFILE_HEROKU: String = "heroku"
        const val SPRING_PROFILE_AWS_ECS: String = "aws-ecs"
        const val SPRING_PROFILE_AZURE: String = "azure"
        const val SPRING_PROFILE_SWAGGER: String = "swagger"
        const val SPRING_PROFILE_NO_LIQUIBASE: String = "no-liquibase"
        const val SPRING_PROFILE_K8S: String = "k8s"
    }
}