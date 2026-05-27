# ===== 构建阶段 =====
FROM node:20-alpine AS builder
WORKDIR /build

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ===== 运行阶段 =====
FROM nginx:1.27-alpine

# 创建非 root 用户
RUN adduser -D -g '' -h /usr/share/nginx/html -s /sbin/nologin appuser

COPY --from=builder /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 创建 Nginx 运行时所需目录并授权
RUN mkdir -p /var/cache/nginx/client_temp \
    /var/cache/nginx/proxy_temp \
    /var/cache/nginx/fastcgi_temp \
    /var/cache/nginx/uwsgi_temp \
    /var/cache/nginx/scgi_temp \
    && chown -R appuser:appuser /var/cache/nginx \
    && chown -R appuser:appuser /usr/share/nginx/html \
    && chown -R appuser:appuser /etc/nginx/conf.d \
    && touch /var/run/nginx.pid \
    && chown -R appuser:appuser /var/run/nginx.pid

# 修改 nginx 配置以非 root 运行
RUN sed -i 's/user  nginx;/user  appuser;/g' /etc/nginx/nginx.conf

EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget -qO- http://localhost:80/ || exit 1

USER appuser

CMD ["nginx", "-g", "daemon off;"]
