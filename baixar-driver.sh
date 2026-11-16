#!/bin/bash
# Baixa o driver JDBC do MySQL (Maven Central) e confere o hash SHA-1 publicado.
cd "$(dirname "$0")"
V=9.7.0
JAR="lib/mysql-connector-j-$V.jar"
BASE="https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/$V/mysql-connector-j-$V.jar"
[ -f "$JAR" ] && exit 0
mkdir -p lib
curl -sSfL -o "$JAR" "$BASE" || { echo "Falha ao baixar o driver."; rm -f "$JAR"; exit 1; }
ESPERADO=$(curl -sSfL "$BASE.sha1")
OBTIDO=$(shasum "$JAR" | cut -d' ' -f1)
if [ "$ESPERADO" != "$OBTIDO" ]; then
  echo "Hash do driver nao confere. Arquivo removido."; rm -f "$JAR"; exit 1
fi
echo "Driver baixado e conferido: $JAR"
