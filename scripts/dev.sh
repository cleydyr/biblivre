#!/bin/bash

# Biblivre Development Helper Script
# Usage: ./scripts/dev.sh <command>

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
DEBUG_PORT=${DEBUG_PORT:-5005}
APP_PORT=${APP_PORT:-8090}
MAVEN_PROFILES="developer"

print_usage() {
    echo -e "${GREEN}Biblivre Development Helper${NC}"
    echo "Usage: $0 <command>"
    echo ""
    echo "Commands:"
    echo "  debug              Run with debugger on port $DEBUG_PORT"
    echo "  debug-suspend      Run with debugger (suspend until connected)"
    echo "  hot-reload         Run with hot reload enabled"
    echo "  profile <name>     Run with specific Maven profile"
    echo "  full-stack         Run full stack (backend + frontend + database)"
    echo "  backend-only       Run only backend with external database"
    echo ""
    echo "Environment Variables:"
    echo "  DEBUG_PORT         Debug port (default: 5005)"
    echo "  APP_PORT          Application port (default: 8090)"
    echo "  DB_HOST           Database host (default: localhost)"
    echo "  DB_PORT           Database port (default: 5432)"
}

check_prerequisites() {
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}Error: Maven not found. Please install Maven.${NC}"
        exit 1
    fi
    
    if ! command -v java &> /dev/null; then
        echo -e "${RED}Error: Java not found. Please install Java 21+.${NC}"
        exit 1
    fi
    
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}Error: Docker not found. Please install Docker.${NC}"
        exit 1
    fi
    
    if ! docker compose version &> /dev/null; then
        echo -e "${RED}Error: 'docker compose' not available. Please install Docker Compose v2.${NC}"
        exit 1
    fi
    
    # Check Java version
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$java_version" -lt 21 ]; then
        echo -e "${YELLOW}Warning: Java 21+ recommended. Current version: $java_version${NC}"
    fi
}

setup_environment() {
    export MAVEN_OPTS="-XX:+UnlockExperimentalVMOptions --enable-preview"
    export SPRING_PROFILES_ACTIVE="$MAVEN_PROFILES"
}

ensure_database() {
    echo -e "${GREEN}Ensuring PostgreSQL database is running...${NC}"
    
    # Check if container exists and is running
    if ! docker ps --format '{{.Names}}' | grep -q biblivre-dev-db; then
        echo -e "${YELLOW}Starting PostgreSQL container...${NC}"
        docker compose -f docker-compose.dev.yml up -d database
        
        # Wait for database to be ready
        echo -e "${YELLOW}Waiting for database to be ready...${NC}"
        timeout=60
        while [ $timeout -gt 0 ] && ! docker exec biblivre-dev-db pg_isready -U biblivre -d biblivre4 > /dev/null 2>&1; do
            sleep 2
            timeout=$((timeout-2))
        done
        
        if [ $timeout -le 0 ]; then
            echo -e "${RED}Database failed to start within 60 seconds${NC}"
            exit 1
        fi
        
        echo -e "${GREEN}Database is ready!${NC}"
    else
        echo -e "${YELLOW}Database container is already running${NC}"
    fi
}

run_debug() {
    local suspend=${1:-n}
    ensure_database
    
    echo -e "${GREEN}Starting Spring Boot with debugger...${NC}"
    echo -e "${YELLOW}Debug port: $DEBUG_PORT${NC}"
    echo -e "${YELLOW}Application port: $APP_PORT${NC}"
    
    if [ "$suspend" = "y" ]; then
        echo -e "${YELLOW}Application will suspend until debugger connects${NC}"
    fi
    
    export MAVEN_OPTS="$MAVEN_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=$suspend,address=*:$DEBUG_PORT"
    
    mvn spring-boot:run \
        -Dspring-boot.run.profiles="$MAVEN_PROFILES" \
        -Dspring-boot.run.jvmArguments="$MAVEN_OPTS"
}

run_hot_reload() {
    ensure_database
    
    echo -e "${GREEN}Starting Spring Boot with hot reload...${NC}"
    echo -e "${YELLOW}Application port: $APP_PORT${NC}"
    
    export MAVEN_OPTS="$MAVEN_OPTS -Dspring.devtools.restart.enabled=true"
    
    mvn spring-boot:run \
        -Dspring-boot.run.profiles="$MAVEN_PROFILES" \
        -Dspring-boot.run.jvmArguments="$MAVEN_OPTS"
}

run_with_profile() {
    local profile=$1
    ensure_database
    
    echo -e "${GREEN}Starting Spring Boot with profile: $profile${NC}"
    
    mvn spring-boot:run \
        -Dspring-boot.run.profiles="$profile" \
        -Dspring-boot.run.jvmArguments="$MAVEN_OPTS"
}

run_full_stack() {
    echo -e "${GREEN}Starting full stack development environment...${NC}"
    
    # Start database and supporting services using full docker-compose
    echo -e "${YELLOW}Starting full environment with Docker Compose...${NC}"
    docker compose up -d
    
    echo -e "${GREEN}Full stack environment started!${NC}"
    echo -e "${YELLOW}Application: http://localhost:8080${NC}"
    echo -e "${YELLOW}Database: localhost:5432${NC}"
    echo -e "${YELLOW}Debug port: 8000${NC}"
}

run_backend_only() {
    ensure_database
    
    echo -e "${GREEN}Starting backend with development database...${NC}"
    run_debug n
}

main() {
    if [ $# -eq 0 ]; then
        print_usage
        exit 1
    fi
    
    check_prerequisites
    setup_environment
    
    case "$1" in
        debug)
            run_debug n
            ;;
        debug-suspend)
            run_debug y
            ;;
        hot-reload)
            run_hot_reload
            ;;
        profile)
            if [ -z "$2" ]; then
                echo -e "${RED}Error: Profile name required${NC}"
                exit 1
            fi
            run_with_profile "$2"
            ;;
        full-stack)
            run_full_stack
            ;;
        backend-only)
            run_backend_only
            ;;
        help|--help|-h)
            print_usage
            ;;
        *)
            echo -e "${RED}Error: Unknown command '$1'${NC}"
            print_usage
            exit 1
            ;;
    esac
}

main "$@"