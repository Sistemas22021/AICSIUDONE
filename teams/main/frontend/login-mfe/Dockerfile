# ─────────────────────────────────────────────────────────────────────────────
# Dockerfile — Frontend (Login MFE y Consumer App)
#
# Multi-stage build:
#   Stage 1 (builder): npm install + vite build
#   Stage 2 (runtime): nginx sirve el /dist estático
# ─────────────────────────────────────────────────────────────────────────────

FROM node:20-alpine AS builder

WORKDIR /app

# Copiar solo package.json primero para aprovechar cache de capas
COPY package.json package-lock.json* ./
RUN npm ci --prefer-offline

# Copiar el resto y compilar
COPY . .
RUN npm run build

# ── Stage 2: nginx runtime ───────────────────────────────────────────────────
FROM nginx:alpine AS runtime

# Copiar el build al directorio de nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# Configuración de nginx para SPA (React Router)
# Redirige todas las rutas al index.html para que React Router las maneje
RUN echo 'server { \
    listen 80; \
    root /usr/share/nginx/html; \
    index index.html; \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
