# ===== 构建阶段 =====
FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# ===== 运行阶段 =====
FROM nginx:alpine

# 非 root 用户
RUN addgroup -g 101 -S appgroup && \
    adduser -S -u 1001 -G appgroup appuser

COPY --from=builder /app/dist /usr/share/nginx/html

# nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget -qO- http://localhost:80/ || exit 1

USER appuser

CMD ["nginx", "-g", "daemon off;"]
