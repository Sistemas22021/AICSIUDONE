# ─────────────────────────────────────────────────────────────────────────────
# Dockerfile — Frontend (Login MFE y Consumer App)
#
# Multi-stage build:
#   Stage 1 (Deps): npm install
#   Stage 2 (builder): npm run build
#   Stage 3 (runtime): node server.js
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: deps ──
FROM node:22-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci

# ── Stage 2: builder ──
FROM node:20-alpine AS builder
WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules
COPY . .

ARG NEXT_PUBLIC_API_GATEWAY_URL
ARG NEXT_PUBLIC_LOGIN_MFE_URL
ENV NEXT_PUBLIC_API_GATEWAY_URL=$NEXT_PUBLIC_API_GATEWAY_URL
ENV NEXT_PUBLIC_LOGIN_MFE_URL=$NEXT_PUBLIC_LOGIN_MFE_URL

# Desactiva telemetría de Next.js durante la compilación
ENV NEXT_TELEMETRY_DISABLED=1

RUN npm run build

# ── Stage 3: runtime ──
FROM node:22-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

RUN addgroup --system --gid 1001 nodejs \
 && adduser --system --uid 1001 nextjs

# Requiere output: 'standalone' en next.config.js/ts para este layout.
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs
EXPOSE 3000
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

CMD ["node", "server.js"]
