#!/usr/bin/env bash
set -euo pipefail

PORT="${VITE_DEV_SERVER_PORT:-5173}"
VITE_DEV_SERVER="http://localhost:${PORT}"

is_port_in_use() {
	lsof -iTCP:"${PORT}" -sTCP:LISTEN -t >/dev/null 2>&1
}

is_vite_running() {
	curl -sf "${VITE_DEV_SERVER}/@vite/client" >/dev/null 2>&1
}

if is_port_in_use; then
	if is_vite_running; then
		echo "Vite dev server is already running at ${VITE_DEV_SERVER}."
		echo "Reusing the existing server. Browse the SPA at http://localhost:8090/spa/"
		exit 0
	fi

	echo "Port ${PORT} is in use, but it does not look like a Vite dev server." >&2
	echo "Free the port or set BIBLIVRE_VITE_DEV_SERVER to your dev server URL." >&2
	exit 1
fi

exec vite
