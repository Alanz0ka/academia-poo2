#!/bin/bash
# Compila o projeto com o Java 25. Saida em out/.
cd "$(dirname "$0")"
# usa o JDK 25 instalado (no Mac, java_home escolhe a versao pedida)
if [ -x /usr/libexec/java_home ]; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 25)" || { echo "Java 25 nao encontrado."; exit 1; }
  export PATH="$JAVA_HOME/bin:$PATH"
fi
./baixar-driver.sh || exit 1
mkdir -p out
javac --release 25 -encoding UTF-8 -cp "lib/*" -d out $(find src teste -name '*.java') && echo "Compilado com Java 25 em out/"
