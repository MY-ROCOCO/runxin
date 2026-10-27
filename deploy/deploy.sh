#!/usr/bin/env bash
# 后端启停脚本
# 用法: ./deploy.sh {start|stop|restart|status}
# 可通过环境变量覆盖:
#   APP_HOME=/www/wwwroot/data
#   APP_PORT=82
#   JAR_NAME=$APP_HOME/app.jar
#   CONFIG_FILE=$APP_HOME/application-prod.yml
#   JAVA_BIN=java
#   JAVA_OUT=$APP_HOME/app.log
#   PID_FILE=$APP_HOME/app.pid

set -uo pipefail

APP_NAME="${APP_NAME:-app}"
APP_HOME="${APP_HOME:-/www/wwwroot/data}"
APP_PORT="${APP_PORT:-82}"
JAR_NAME="${JAR_NAME:-${APP_HOME}/${APP_NAME}.jar}"
CONFIG_FILE="${CONFIG_FILE:-${APP_HOME}/application-prod.yml}"
JAVA_BIN="${JAVA_BIN:-java}"
JAVA_OUT="${JAVA_OUT:-${APP_HOME}/app.log}"
PID_FILE="${PID_FILE:-${APP_HOME}/app.pid}"
HEALTH_CHECK_URL="${HEALTH_CHECK_URL:-http://127.0.0.1:${APP_PORT}}"
APP_START_TIMEOUT="${APP_START_TIMEOUT:-60}"

usage() {
    echo "Usage: $0 {start|stop|restart|status}"
    exit 2
}

health_check() {
    local waited=0
    echo "checking ${HEALTH_CHECK_URL}"
    while true; do
        status_code="$(curl -L -o /dev/null --connect-timeout 5 -s -w '%{http_code}' "${HEALTH_CHECK_URL}" 2>/dev/null || true)"
        if [ "${status_code}" = "200" ]; then
            echo "check ${HEALTH_CHECK_URL} success"
            return 0
        fi
        waited=$((waited + 1))
        echo -ne "\rWait app to pass health check: ${waited}s..."
        if [ "${waited}" -ge "${APP_START_TIMEOUT}" ]; then
            echo ""
            echo "app start failed after ${APP_START_TIMEOUT}s"
            return 1
        fi
        sleep 1
    done
}

start_application() {
    mkdir -p "${APP_HOME}"
    if [ -f "${PID_FILE}" ] && kill -0 "$(cat "${PID_FILE}")" 2>/dev/null; then
        echo "application already running, pid=$(cat "${PID_FILE}")"
        return 0
    fi
    if [ ! -f "${JAR_NAME}" ]; then
        echo "jar not found: ${JAR_NAME}"
        exit 1
    fi
    if [ ! -f "${CONFIG_FILE}" ]; then
        echo "config not found: ${CONFIG_FILE}"
        exit 1
    fi
    echo "starting ${JAR_NAME}"
    nohup "${JAVA_BIN}" -Dspring.config.location="${CONFIG_FILE}" -jar "${JAR_NAME}" >"${JAVA_OUT}" 2>&1 &
    echo $! >"${PID_FILE}"
    health_check
}

stop_application() {
    if [ ! -f "${PID_FILE}" ]; then
        echo "no pid file: ${PID_FILE}"
        return 0
    fi
    pid="$(cat "${PID_FILE}")"
    if ! kill -0 "${pid}" 2>/dev/null; then
        echo "process not running, remove stale pid file"
        rm -f "${PID_FILE}"
        return 0
    fi
    echo "stopping pid ${pid}"
    kill "${pid}" 2>/dev/null || true
    for _ in $(seq 1 30); do
        if ! kill -0 "${pid}" 2>/dev/null; then
            rm -f "${PID_FILE}"
            echo "stopped"
            return 0
        fi
        sleep 1
    done
    echo "force kill ${pid}"
    kill -9 "${pid}" 2>/dev/null || true
    rm -f "${PID_FILE}"
}

status_application() {
    if [ -f "${PID_FILE}" ] && kill -0 "$(cat "${PID_FILE}")" 2>/dev/null; then
        echo "running, pid=$(cat "${PID_FILE}")"
    else
        echo "not running"
        exit 1
    fi
}

case "${1:-}" in
    start) start_application ;;
    stop) stop_application ;;
    restart) stop_application; start_application ;;
    status) status_application ;;
    *) usage ;;
esac
