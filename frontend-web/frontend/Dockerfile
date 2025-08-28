# ---------- Build stage ----------
FROM node:20-alpine AS build
WORKDIR /app

# Installeer dependencies
COPY package*.json ./
RUN npm ci

# Kopieer app code en build voor production (gebruikt environment.prod.ts)
COPY . .
RUN npm run build -- --configuration=production

# ---------- Runtime stage ----------
FROM nginx:alpine

# Verwijder default config en voeg SPA-config toe
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Kopieer Angular build output (let op: vervang <projectnaam> uit angular.json)
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
