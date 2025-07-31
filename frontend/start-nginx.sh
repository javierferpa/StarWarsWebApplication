#!/bin/sh

echo "=== Railway Environment Detection ==="
echo "RAILWAY_ENVIRONMENT: $RAILWAY_ENVIRONMENT"
echo "RAILWAY_SERVICE_NAME: $RAILWAY_SERVICE_NAME" 
echo "RAILWAY_PROJECT_ID: $RAILWAY_PROJECT_ID"
echo "BACKEND_URL env var: $BACKEND_URL"

# For Railway deployment, use the backend service URL
# For local development, use the default backend URL
if [ -n "$RAILWAY_ENVIRONMENT" ] || [ -n "$RAILWAY_SERVICE_NAME" ] || [ -n "$RAILWAY_PROJECT_ID" ] || [ -n "$RAILWAY_DEPLOYMENT_ID" ]; then
    # Railway environment - try private networking first, fallback to public URL
    BACKEND_URL=${BACKEND_URL:-"https://starwars-backend-production.up.railway.app"}
    echo "✅ Railway environment detected - using public backend URL"
else
    # Local development - use docker internal networking
    BACKEND_URL=${BACKEND_URL:-"http://backend:8080"}
    echo "🏠 Local development environment"
fi

echo "🔗 Using backend URL: $BACKEND_URL"

# Create nginx config with the correct backend URL
cat > /etc/nginx/conf.d/default.conf << EOF
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Proxy API requests to backend service
    location /api/ {
        proxy_pass ${BACKEND_URL}/api/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_buffering off;
        
        # SSL settings for HTTPS backends
        proxy_ssl_verify off;
        proxy_ssl_server_name on;
    }

    # Serve static files and handle Angular routing (catch-all)
    location / {
        try_files \$uri \$uri/ /index.html;
    }
}
EOF

# Start nginx
exec nginx -g "daemon off;"
