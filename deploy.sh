#!/bin/bash
set -e

echo "🚀 开始部署 PetServicePlatform..."

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 检查 secrets 目录
if [ ! -d "./secrets" ]; then
    echo "📁 创建 secrets 目录..."
    mkdir -p ./secrets
fi

# 检查密钥文件
if [ ! -f "./secrets/db_password.txt" ]; then
    echo "📝 创建默认数据库密码文件（请在生产环境中修改）..."
    echo "123456" > ./secrets/db_password.txt
fi

if [ ! -f "./secrets/jwt_secret.txt" ]; then
    echo "📝 创建默认 JWT 密钥文件（请在生产环境中修改）..."
    echo "replace-with-a-secure-secret-key-1234567890" > ./secrets/jwt_secret.txt
fi

# 选择部署模式
MODE=${1:-dev}

if [ "$MODE" = "prod" ]; then
    echo "🏭 使用生产环境配置..."
    COMPOSE_FILE="compose.prod.yaml"
else
    echo "🔧 使用开发环境配置..."
    COMPOSE_FILE="docker-compose.yml"
fi

# 重新构建并启动
echo "🏗️  构建并启动服务..."
docker compose -f "$COMPOSE_FILE" up -d --build

# 等待服务就绪
echo "⏳ 等待服务就绪..."
sleep 15

# 显示服务状态
echo ""
echo "📊 服务状态："
docker compose -f "$COMPOSE_FILE" ps

echo ""
echo "✅ 部署完成！"
echo "   前端: http://localhost:8081"
echo "   后端: http://localhost:8080"
echo "   健康检查: http://localhost:8080/actuator/health"
echo ""
echo "💡 查看日志: docker compose -f $COMPOSE_FILE logs -f"
echo "💡 停止服务: docker compose -f $COMPOSE_FILE down"
