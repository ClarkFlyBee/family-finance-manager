# frontend/Dockerfile

# 构建阶段
FROM node:20-alpine AS builder

WORKDIR /app

# 复制 package.json 和源码（不需要 frontend/ 前缀）
COPY package*.json ./
RUN npm config set registry https://registry.npmmirror.com && \
    npm install

# 复制所有文件（当前目录就是 frontend/）
COPY . .

# 构建
RUN npm run build

# 第二阶段：Nginx
FROM nginx:alpine

# 复制 nginx.conf（它就在当前目录）
COPY nginx.conf /etc/nginx/nginx.conf

# 删除默认配置
RUN rm -rf /etc/nginx/conf.d

# 复制构建结果
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]