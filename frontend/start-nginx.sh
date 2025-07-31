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
    echo "Railway environment detected - using public backend URL"
else
    # Local development - use docker internal networking
    BACKEND_URL=${BACKEND_URL:-"http://backend:8080"}
    echo "Local development environment"
fi

echo "Using backend URL: $BACKEND_URL"

# Extract backend host from URL for proper Host header
BACKEND_HOST=$(echo "$BACKEND_URL" | sed 's|https\?://||' | sed 's|/.*||')
echo "Backend host: $BACKEND_HOST"

# Create nginx config with the correct backend URL
echo "Creating nginx configuration..."
cat > /etc/nginx/conf.d/default.conf << EOF
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Add custom headers for debugging
    add_header X-Backend-URL "${BACKEND_URL}" always;
    add_header X-Proxy-Config "Railway-Configured" always;

    # Proxy API requests to backend service
    location /api/ {
        # Proxy to the backend service
        proxy_pass ${BACKEND_URL}/api/;
        
        # Critical: Set the Host header to the backend domain
        proxy_set_header Host ${BACKEND_HOST};
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-Host \$server_name;
        proxy_set_header X-Forwarded-Port 443;
        
        # Disable buffering for real-time responses
        proxy_buffering off;
        proxy_cache off;
        
        # SSL settings for HTTPS backends
        proxy_ssl_verify off;
        proxy_ssl_server_name on;
        proxy_ssl_protocols TLSv1.2 TLSv1.3;
        
        # Redirect handling
        proxy_redirect off;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Add debug headers
        add_header X-Proxied-To "${BACKEND_URL}" always;
        add_header X-Backend-Host "${BACKEND_HOST}" always;
    }

    # Handle Angular routes - catch all other requests and serve index.html
    location / {
        try_files \$uri \$uri/ /index.html;
        
        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, no-transform";
        }
    }

}
EOF

echo "Nginx configuration created"
echo "Configuration preview:"
echo "----------------------------------------"
cat /etc/nginx/conf.d/default.conf
echo "----------------------------------------"

# Test nginx configuration
echo "Testing nginx configuration..."
nginx -t

if [ $? -eq 0 ]; then
    echo "Nginx configuration is valid"
else
    echo "Nginx configuration has errors!"
    exit 1
fi

# Start nginx
echo "Starting nginx..."
exec nginx -g "daemon off;"
