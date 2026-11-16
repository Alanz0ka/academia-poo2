#!/bin/bash
# Abre o programa. O banco precisa estar rodando (iniciar-banco.sh).
cd "$(dirname "$0")"
[ -d out ] || ./build.sh
java -cp "out:lib/*" academia.Main
