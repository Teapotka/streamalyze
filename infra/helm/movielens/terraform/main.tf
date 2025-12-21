terraform {
  required_version = ">= 1.5.0"

  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.30"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.13"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

resource "kubernetes_namespace" "movielens" {
  metadata {
    name = "movielens"
  }
}

# Optional: override image tags, etc.
locals {
  movielens_values = yamldecode(file("${path.module}/values-override.yaml"))
}

resource "helm_release" "movielens" {
  name       = "movielens"
  chart      = "${path.module}/../helm/movielens"
  namespace  = kubernetes_namespace.movielens.metadata[0].name
  create_namespace = false

  # base values.yaml from chart is loaded automatically; we pass overrides:
  values = [
    file("${path.module}/values-override.yaml")
  ]
}
