#!/bin/bash
# Abre o programa com o Java 25. O banco precisa estar rodando (iniciar-banco.sh).
cd "$(dirname "$0")"
if [ -x /usr/libexec/java_home ]; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 25)" || { echo "Java 25 nao encontrado."; exit 1; }
  export PATH="$JAVA_HOME/bin:$PATH"
fi
[ -d out ] || ./build.sh
java -cp "out:lib/*" academia.Main
