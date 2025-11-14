#!/usr/bin/bash


# ===== CONFIGURAÇÕES =====
#CONTAINER_NAME="mariadb"
CONTAINER_NAME="grocerystore-mariadb-1"
DB_NAME="mydatabase"
DB_USER="root"
DB_PASS="verysecret"

# Caminho ABSOLUTO do arquivo no Windows
SQL_FILE="create-table.sql"
# ==========================

echo "📦 Importando tabelas a partir de:"
echo "   $SQL_FILE"
echo ""

# Verifica se o arquivo existe
if [ ! -f "$SQL_FILE" ]; then
  echo "❌ Arquivo não encontrado nesse caminho!"
  exit 1
fi

# Executa o SQL dentro do container
docker exec -i "$CONTAINER_NAME" \
  mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" < "$SQL_FILE"

# Checa status da execução
if [ $? -eq 0 ]; then
  echo "✅ Importação concluída com sucesso!"
else
  echo "❌ Erro ao importar tabelas."
fi