# build stage
FROM node:18-alpine AS build
WORKDIR /src
COPY . .
RUN npm ci && npm run build --prod

# runtime stage
FROM nginx:alpine
COPY --from=build /src/dist /usr/share/nginx/html
# pass backend url to the app (optional replace-at-runtime script pattern)
ENV API_URL=http://backend:8080
EXPOSE 80
