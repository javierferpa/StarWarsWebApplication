#!/bin/sh

# Default backend URL for local development
BACKEND_URL=${BACKEND_URL:-"http://backend:8080"}

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
