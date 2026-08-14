#!/usr/bin/env bash
# Smoke-start Biblivre against docker-compose.dev.yml DB.
# Prefers the packaged WAR (java -jar) so CI does not pay a second Maven
# generate-resources/Yarn cycle; falls back to spring-boot:run for local use.
#
# Fails on APPLICATION FAILED TO START (the RestClient.Builder class of bug) and
# succeeds only after the app reports Started and serves HTTP on SMOKE_PORT.
#
# Usage: ./scripts/smoke-dev.sh
# Env:
#   SMOKE_PORT   HTTP port (default 18090)
#   SMOKE_TIMEOUT_SECONDS  max wait (default 180)
#   SKIP_DB_START  set to 1 to skip docker compose DB bring-up

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SMOKE_PORT="${SMOKE_PORT:-18090}"
SMOKE_TIMEOUT_SECONDS="${SMOKE_TIMEOUT_SECONDS:-180}"
SKIP_DB_START="${SKIP_DB_START:-0}"
LOG_FILE="${TMPDIR:-/tmp}/biblivre-smoke-dev.$$.log"
SERVER_PID=""
PACKAGED_WAR="${ROOT_DIR}/target/Biblivre6.war"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

stop_server() {
	if [[ -z "${SERVER_PID}" ]]; then
		return
	fi
	echo -e "${YELLOW}Stopping smoke server (pid ${SERVER_PID})...${NC}"
	# spring-boot:run leaves a Java child; kill the Maven process tree. java -jar
	# is a single process — pkill is a no-op when there are no children.
	if kill -0 "${SERVER_PID}" 2>/dev/null; then
		pkill -TERM -P "${SERVER_PID}" 2>/dev/null || true
		kill -TERM "${SERVER_PID}" 2>/dev/null || true
		wait "${SERVER_PID}" 2>/dev/null || true
	fi
	SERVER_PID=""
}

cleanup() {
	local exit_code=$?
	stop_server
	if [[ "${exit_code}" -ne 0 && -f "${LOG_FILE}" ]]; then
		echo -e "${RED}---- smoke log (tail) ----${NC}"
		tail -n 80 "${LOG_FILE}" || true
	fi
	rm -f "${LOG_FILE}"
	exit "${exit_code}"
}
trap cleanup EXIT INT TERM

ensure_database() {
	if [[ "${SKIP_DB_START}" == "1" ]]; then
		echo -e "${YELLOW}SKIP_DB_START=1 — assuming PostgreSQL is already reachable${NC}"
		return
	fi
	echo -e "${GREEN}Ensuring PostgreSQL database is running...${NC}"
	if ! docker ps --format '{{.Names}}' | grep -q '^biblivre-dev-db$'; then
		echo -e "${YELLOW}Starting PostgreSQL container...${NC}"
		docker compose -f docker-compose.dev.yml up -d database
	else
		echo -e "${YELLOW}Database container is already running${NC}"
	fi

	local timeout=60
	while [[ "${timeout}" -gt 0 ]] &&
		! docker exec biblivre-dev-db pg_isready -U biblivre -d biblivre4 >/dev/null 2>&1; do
		sleep 2
		timeout=$((timeout - 2))
	done
	if [[ "${timeout}" -le 0 ]]; then
		echo -e "${RED}Database failed to become ready within 60 seconds${NC}"
		exit 1
	fi
	echo -e "${GREEN}Database is ready${NC}"
}

start_server() {
	export MAVEN_OPTS="${MAVEN_OPTS:--XX:+UnlockExperimentalVMOptions --enable-preview}"
	export BIBLIVRE_CORS_ENABLED="${BIBLIVRE_CORS_ENABLED:-true}"

	if command -v lsof >/dev/null 2>&1; then
		if lsof -t -iTCP:"${SMOKE_PORT}" -sTCP:LISTEN >/dev/null 2>&1; then
			echo -e "${RED}Port ${SMOKE_PORT} is already in use. Stop the existing listener and retry.${NC}"
			exit 1
		fi
	elif command -v ss >/dev/null 2>&1; then
		if ss -H -ltn "sport = :${SMOKE_PORT}" | grep -q .; then
			echo -e "${RED}Port ${SMOKE_PORT} is already in use. Stop the existing listener and retry.${NC}"
			exit 1
		fi
	else
		echo -e "${YELLOW}Skipping preflight port check: neither lsof nor ss is available.${NC}"
	fi

	echo -e "${YELLOW}Log: ${LOG_FILE}${NC}"

	# Keep production-default embedding.provider=openai_compatible (application.yml).
	# Do not activate the test profile — that switches to hashing and hides this smoke.
	local -a run_arguments=(
		"--server.port=${SMOKE_PORT}"
		"--spring.devtools.restart.enabled=false"
		"--spring.devtools.livereload.enabled=false"
		"--spring.profiles.active=developer"
	)

	if [[ -f "${PACKAGED_WAR}" ]]; then
		echo -e "${GREEN}Starting packaged app on port ${SMOKE_PORT}...${NC}"
		# shellcheck disable=SC2086
		java ${MAVEN_OPTS} -jar "${PACKAGED_WAR}" "${run_arguments[@]}" >"${LOG_FILE}" 2>&1 &
		SERVER_PID=$!
		return
	fi

	echo -e "${GREEN}Starting Spring Boot (developer profile) on port ${SMOKE_PORT}...${NC}"
	mvn -B -ntp spring-boot:run \
		-Dspring-boot.run.profiles=developer \
		-Dskip.yarn=true \
		-Dopenapi.generator.maven.plugin.skip=true \
		-Dspring-boot.run.jvmArguments="${MAVEN_OPTS}" \
		-Dspring-boot.run.arguments="${run_arguments[*]}" \
		>"${LOG_FILE}" 2>&1 &
	SERVER_PID=$!
}

await_started() {
	local deadline=$((SECONDS + SMOKE_TIMEOUT_SECONDS))
	echo -e "${YELLOW}Waiting up to ${SMOKE_TIMEOUT_SECONDS}s for application start...${NC}"

	while ((SECONDS < deadline)); do
		if grep -q 'APPLICATION FAILED TO START' "${LOG_FILE}" 2>/dev/null; then
			echo -e "${RED}Application failed to start${NC}"
			if grep -q 'RestClient\$Builder' "${LOG_FILE}" 2>/dev/null; then
				echo -e "${RED}Missing RestClient.Builder bean (add spring-boot-starter-restclient)${NC}"
			fi
			exit 1
		fi

		if grep -qE 'Started .+ in .+ seconds' "${LOG_FILE}" 2>/dev/null; then
			echo -e "${GREEN}Application reported Started${NC}"
			return 0
		fi

		if ! kill -0 "${SERVER_PID}" 2>/dev/null; then
			echo -e "${RED}Smoke server process exited before the app started${NC}"
			exit 1
		fi

		sleep 2
	done

	echo -e "${RED}Timed out waiting for application start${NC}"
	exit 1
}

http_smoke() {
	local url="http://127.0.0.1:${SMOKE_PORT}/"
	echo -e "${YELLOW}HTTP smoke: ${url}${NC}"
	# Home may redirect; accept any 2xx/3xx.
	local code
	code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 15 "${url}" || true)"
	if [[ ! "${code}" =~ ^[23][0-9][0-9]$ ]]; then
		echo -e "${RED}Unexpected HTTP status from ${url}: ${code:-none}${NC}"
		exit 1
	fi
	echo -e "${GREEN}HTTP smoke OK (${code})${NC}"
}

ensure_database
start_server
await_started
http_smoke
echo -e "${GREEN}Developer-mode smoke passed${NC}"
