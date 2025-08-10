# Biblivre Development Shortcuts
# Usage: make <command>

# Default target
.DEFAULT_GOAL := help

# Variables
MAVEN_OPTS := -XX:+UnlockExperimentalVMOptions --enable-preview
DEBUG_OPTS := -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
DOCKER_COMPOSE_FILE := docker-compose.yml
DOCKER_COMPOSE := docker compose

# Colors for output
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[1;33m
NC := \033[0m # No Color

.PHONY: help dev debug build test clean format check docker-up docker-down frontend-dev frontend-build

help: ## Show this help message
	@echo "$(GREEN)Biblivre Development Shortcuts$(NC)"
	@echo "================================"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "$(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'

# Development Commands
dev: ## Run Spring Boot application with hot reload
	@$(MAKE) db-start
	@echo "$(GREEN)Starting Spring Boot application with hot reload...$(NC)"
	@export MAVEN_OPTS="$(MAVEN_OPTS)" && mvn spring-boot:run -Dspring-boot.run.profiles=developer

debug: ## Run Spring Boot application with debug port 5005 exposed
	@$(MAKE) db-start
	@echo "$(GREEN)Starting Spring Boot application with debug on port 5005...$(NC)"
	@echo "$(YELLOW)Connect your IDE debugger to localhost:5005$(NC)"
	@export MAVEN_OPTS="$(MAVEN_OPTS) $(DEBUG_OPTS)" && mvn spring-boot:run -Dspring-boot.run.profiles=developer

debug-suspend: ## Run Spring Boot application with debug port 5005, suspend until debugger connects
	@$(MAKE) db-start
	@echo "$(GREEN)Starting Spring Boot application with debug (suspended) on port 5005...$(NC)"
	@echo "$(YELLOW)Application will wait for debugger connection on localhost:5005$(NC)"
	@export MAVEN_OPTS="$(MAVEN_OPTS) -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005" && mvn spring-boot:run -Dspring-boot.run.profiles=developer

# Build Commands
build: ## Clean build the entire project
	@echo "$(GREEN)Building project...$(NC)"
	@mvn clean package

build-fast: ## Fast build without tests and checks
	@echo "$(GREEN)Fast build without tests...$(NC)"
	@mvn clean package -DskipTests -Dspotless.check.skip=true -Dspotbugs.skip=true -Dpmd.skip=true

build-docker: ## Build with Docker Compose profile
	@echo "$(GREEN)Building for Docker Compose...$(NC)"
	@mvn clean package -P docker-compose

# Testing Commands
test: ## Run all Java tests
	@echo "$(GREEN)Running Java tests...$(NC)"
	@mvn test -P developer

test-specific: ## Run specific test class (usage: make test-specific TEST=OrderDAOImplTest)
	@echo "$(GREEN)Running test: $(TEST)$(NC)"
	@mvn test -P developer -Dtest=$(TEST)

test-frontend: ## Run frontend tests
	@echo "$(GREEN)Running frontend tests...$(NC)"
	@cd src/main/frontend && yarn test

# Code Quality Commands
format: ## Format code using Spotless
	@echo "$(GREEN)Formatting code...$(NC)"
	@mvn spotless:apply

check: ## Run all code quality checks
	@echo "$(GREEN)Running code quality checks...$(NC)"
	@mvn verify

check-security: ## Run security audit
	@echo "$(GREEN)Running security audit...$(NC)"
	@mvn org.owasp:dependency-check-maven:check

# Frontend Commands
frontend-setup: ## Install frontend dependencies
	@echo "$(GREEN)Installing frontend dependencies...$(NC)"
	@cd src/main/frontend && yarn install

frontend-dev: ## Start frontend development server
	@echo "$(GREEN)Starting frontend development server...$(NC)"
	@cd src/main/frontend && yarn start

frontend-build: ## Build frontend for production
	@echo "$(GREEN)Building frontend...$(NC)"
	@cd src/main/frontend && yarn build

frontend-watch: ## Watch frontend files for changes
	@echo "$(GREEN)Watching frontend files...$(NC)"
	@cd src/main/frontend && yarn watch

# Docker Commands
docker-up: ## Start Docker Compose environment
	@echo "$(GREEN)Starting Docker Compose environment...$(NC)"
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) up -d

docker-down: ## Stop Docker Compose environment
	@echo "$(GREEN)Stopping Docker Compose environment...$(NC)"
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) down

docker-logs: ## View Docker Compose logs
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) logs -f

docker-rebuild: ## Rebuild and restart Docker Compose
	@echo "$(GREEN)Rebuilding Docker Compose environment...$(NC)"
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) down
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) build --no-cache
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) up -d

# Database Commands
db-start: ## Start PostgreSQL database container (called automatically by dev/debug)
	@echo "$(GREEN)Ensuring PostgreSQL database is running...$(NC)"
	@if ! docker ps --format '{{.Names}}' | grep -q biblivre-dev-db; then \
		echo "$(YELLOW)Starting PostgreSQL container...$(NC)"; \
		$(DOCKER_COMPOSE) -f docker-compose.dev.yml up -d database; \
		echo "$(YELLOW)Waiting for database to be ready...$(NC)"; \
		timeout=60; \
		while [ $$timeout -gt 0 ] && ! docker exec biblivre-dev-db pg_isready -U biblivre -d biblivre4 > /dev/null 2>&1; do \
			sleep 2; \
			timeout=$$((timeout-2)); \
		done; \
		if [ $$timeout -le 0 ]; then \
			echo "$(RED)Database failed to start within 60 seconds$(NC)"; \
			exit 1; \
		fi; \
		echo "$(GREEN)Database is ready!$(NC)"; \
	else \
		echo "$(YELLOW)Database container is already running$(NC)"; \
	fi

db-stop: ## Stop PostgreSQL database container
	@echo "$(GREEN)Stopping PostgreSQL database container...$(NC)"
	@$(DOCKER_COMPOSE) -f docker-compose.dev.yml down

db-logs: ## View database logs
	@echo "$(GREEN)Viewing database logs...$(NC)"
	@$(DOCKER_COMPOSE) -f docker-compose.dev.yml logs -f database

db-shell: ## Connect to database shell
	@echo "$(GREEN)Connecting to database shell...$(NC)"
	@docker exec -it biblivre-dev-db psql -U biblivre -d biblivre4

db-reset: ## Reset database to initial state
	@echo "$(RED)Resetting database...$(NC)"
	@$(DOCKER_COMPOSE) -f docker-compose.dev.yml down -v
	@$(DOCKER_COMPOSE) -f docker-compose.dev.yml up -d database
	@echo "$(YELLOW)Waiting for database to initialize...$(NC)"
	@timeout=60; \
	while [ $$timeout -gt 0 ] && ! docker exec biblivre-dev-db pg_isready -U biblivre -d biblivre4 > /dev/null 2>&1; do \
		sleep 2; \
		timeout=$$((timeout-2)); \
	done; \
	echo "$(GREEN)Database reset complete!$(NC)"

db-setup: ## Set up local PostgreSQL database (manual installation)
	@echo "$(GREEN)Setting up database...$(NC)"
	@sudo -u postgres psql < sql/createdatabase.sql
	@sudo -u postgres psql biblivre4 < sql/biblivre4.sql

# Utility Commands
clean: ## Clean all build artifacts
	@echo "$(GREEN)Cleaning build artifacts...$(NC)"
	@mvn clean
	@cd src/main/frontend && rm -rf node_modules dist build

logs: ## View application logs
	@echo "$(GREEN)Viewing application logs...$(NC)"
	@tail -f logs/biblivre-logger.log

full-setup: ## Complete development setup
	@echo "$(GREEN)Running complete development setup...$(NC)"
	@$(MAKE) clean
	@$(MAKE) frontend-setup
	@$(MAKE) build

# Development workflow shortcuts
quick-dev: ## Quick development start (build + debug)
	@echo "$(GREEN)Quick development start...$(NC)"
	@$(MAKE) build-fast
	@$(MAKE) debug

full-dev: ## Full development start (clean build + frontend + debug)
	@echo "$(GREEN)Full development start...$(NC)"
	@$(MAKE) clean
	@$(MAKE) frontend-setup
	@$(MAKE) build
	@$(MAKE) debug