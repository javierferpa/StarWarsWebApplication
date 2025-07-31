# build stage
FROM node:22-alpine AS build
WORKDIR /src
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# runtime stage
FROM nginx:alpine
COPY --from=build /src/dist/frontend/browser /usr/share/nginx/html

# Copy startup script
COPY frontend/start-nginx.sh /start-nginx.sh
RUN chmod +x /start-nginx.sh

EXPOSE 80

# Use our custom startup script
CMD ["/start-nginx.sh"]
