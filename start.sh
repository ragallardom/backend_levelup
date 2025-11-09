#!/bin/sh

set -e

if [ -n "${WALLET_B64:-}" ]; then
  mkdir -p /app/wallet
  printf '%s' "$WALLET_B64" | base64 -d > /tmp/wallet.zip
  unzip -o /tmp/wallet.zip -d /app/wallet
  chmod 700 /app/wallet
  export TNS_ADMIN=/app/wallet
fi

: "${PORT:=8080}"
export SERVER_PORT="${PORT}"

exec java -jar /app/app.jar
