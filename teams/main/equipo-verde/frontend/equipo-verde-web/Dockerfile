FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json pnpm-lock.yaml* pnpm-workspace.yaml* ./
RUN npm install -g pnpm && (pnpm install || npm install)
COPY . .
RUN pnpm run build || npm run build

FROM nginx:alpine AS runtime
COPY --from=builder /app/dist /usr/share/nginx/html
RUN echo 'server { listen 80; root /usr/share/nginx/html; index index.html; location /api/ { proxy_pass http://ev-balistic-services:8080/api/; proxy_set_header Host $host; proxy_set_header X-Real-IP $remote_addr; } location / { try_files $uri $uri/ /index.html; } }' > /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
