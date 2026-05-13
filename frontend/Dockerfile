FROM node:20-alpine AS builder
WORKDIR /build

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build


FROM nginx:1.27-alpine

# 创建非 root 用户
RUN adduser -D -g '' -h /usr/share/nginx/html -s /sbin/nologin appuser

COPY --from=builder /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 修改 nginx 配置以非 root 运行
RUN sed -i 's/user  nginx;/user  appuser;/g' /etc/nginx/nginx.conf

EXPOSE 80

USER appuser

CMD ["nginx", "-g", "daemon off;"]
