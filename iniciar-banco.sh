#!/bin/bash
# Sobe (ou religa) o MySQL do projeto em Docker, porta 3307 so no localhost.
# As tabelas sao criadas pelo proprio programa na primeira execucao.
#
# Menor privilegio:
#   --user 999:999            roda como o usuario "mysql", nunca como root
#   --cap-drop=ALL            nenhuma capability (nao precisou adicionar nenhuma)
#   no-new-privileges         o processo nao ganha privilegio extra
#   senha do root aleatoria   so o usuario "academia" e usado pelo programa
if docker ps -a --format '{{.Names}}' | grep -q '^poo2-academia-db$'; then
  docker start poo2-academia-db
else
  docker run -d --name poo2-academia-db -p 127.0.0.1:3307:3306 \
    --user 999:999 --cap-drop=ALL --security-opt=no-new-privileges \
    --memory=512m --pids-limit=200 \
    -e MYSQL_RANDOM_ROOT_PASSWORD=yes -e MYSQL_DATABASE=academia \
    -e MYSQL_USER=academia -e MYSQL_PASSWORD=academia123 mysql:8.4
fi
echo "Aguardando o MySQL aceitar conexoes..."
until docker exec poo2-academia-db mysqladmin ping -uacademia -pacademia123 --silent 2>/dev/null; do sleep 2; done
echo "Banco pronto em localhost:3307."
