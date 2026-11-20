FROM node:20-alpine AS build

WORKDIR /app
COPY package*.json ./
RUN npm ci --no-audit --no-fund && npm cache clean --force

COPY . ./
RUN npm run build

FROM nginxinc/nginx-unprivileged:1.27-alpine

COPY --chown=101:101 nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=101:101 /app/dist/parking-frontend/browser /usr/share/nginx/html

USER 101
EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=6 \
    CMD wget -q -O /dev/null http://127.0.0.1:8080/ || exit 1