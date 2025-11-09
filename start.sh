
set -e

mkdir -p /app/wallet
echo "$WALLET_B64" | base64 -d > /tmp/wallet.zip
unzip -o /tmp/wallet.zip -d /app/wallet
chmod 700 /app/wallet
export TNS_ADMIN=/app/wallet

: "${PORT:=8080}"
export SERVER_PORT="${PORT}"

exec java -jar /app/app.jar
