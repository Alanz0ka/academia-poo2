#!/bin/bash
# Compila o projeto (Java 17 ou mais novo). Saida em out/.
cd "$(dirname "$0")"
./baixar-driver.sh || exit 1
rm -rf out && mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out $(find src teste -name '*.java') && echo "Compilado em out/"
