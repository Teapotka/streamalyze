rootProject.name = "movielens-platform"

include(
    "api-gateway",
    "discovery-service",
    "db-migrations",
    "catalog-service",
    "ratings-service", 
    "recommendation-service", 
    "common-proto"
)