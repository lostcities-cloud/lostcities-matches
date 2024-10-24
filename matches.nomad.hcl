variable "version" {
    type = string
    default = "latest"
}

job "matches" {
    region = "global"
    datacenters = [ "tower-datacenter"]

    update {
        max_parallel = 4
    }

    group "matches" {
        count = 2

        restart {
            attempts = 10
            interval = "5m"
            delay    = "25s"
            mode     = "delay"
        }

        network {
            mode = "bridge"

            port "service-port" {
                to = 8080
            }

            port "management-port" {
                to = 4452
            }
        }

        service {
            name = "matches-service"
            port = "service-port"
            tags = ["urlprefix-/api/matches"]
            #address_mode = "alloc"

            check {
                type = "http"
                port = "management-port"
                path = "/management/matches/actuator/health"
                interval = "30s"
                timeout  = "10s"
                failures_before_critical = 20
                failures_before_warning = 10
            }
        }

        service {
            name = "matches-management"
            port = "management-port"
            tags = ["urlprefix-/management/matches/actuator"]
            #address_mode = "alloc"

            check {
                type = "http"
                port = "management-port"
                path = "/management/matches/actuator/health"
                interval = "30s"
                timeout  = "10s"
                failures_before_critical = 20
                failures_before_warning = 10
            }
        }

        task "matches" {
            driver = "podman"

            env {
                SPRING_PROFILES_ACTIVE = "dev"
            }

            resources {
                cpu    = 100
                memory = 500
            }

            config {
                force_pull = true
                image = "ghcr.io/lostcities-cloud/lostcities-matches:${var.version}"
                ports = ["service-port", "management-port"]
                logging = {
                    driver = "nomad"
                }
            }

            template {
                data        = <<EOF
{{ range service "postgres" }}
POSTGRES_IP="{{ .Address }}"
{{ else }}
{{ end }}
{{ range service "redis" }}
REDIS_IP="{{ .Address }}"
{{ else }}
{{ end }}
{{ range service "rabbitmq" }}
RABBITMQ_IP="{{ .Address }}"
{{ else }}
{{ end }}
EOF
                change_mode = "restart"
                destination = "local/discovery.env"
                env         = true
            }
        }
    }
}
