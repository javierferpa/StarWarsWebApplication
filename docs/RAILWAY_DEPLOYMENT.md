# Railway Deployment Guide

This guide explains how to deploy the Star Wars Web Application to Railway.

## Prerequisites

1. **Railway Account**: Sign up at [railway.app](https://railway.app)
2. **GitHub Repository**: Fork or clone this repository
3. **Domain (Optional)**: For custom domain setup

## Deployment Steps

### 1. Create Railway Project

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Create new project
railway new
```

### 2. Deploy Backend Service

1. **Connect Repository**
   - Go to Railway Dashboard
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your repository

2. **Configure Backend Service**
   - **Root Directory**: `BackEnd`
   - **Build Command**: Automatic (Docker)
   - **Start Command**: Automatic (Docker)

3. **Environment Variables** (if needed)
   ```
   SPRING_PROFILES_ACTIVE=production
   SERVER_PORT=8080
   ```

4. **Custom Domain** (optional)
   - Go to service settings
   - Add custom domain
   - Update DNS records

### 3. Deploy Frontend Service

1. **Add Frontend Service**
   - In same project, click "New Service"
   - Connect same repository
   - **Root Directory**: `frontend`

2. **Configure Frontend Service**
   - **Build Command**: Automatic (Docker)
   - **Start Command**: Automatic (Docker)

3. **Environment Variables**
   ```
   API_URL=https://your-backend-service.railway.app
   ```

### 4. Configure Service Communication

Update `frontend/nginx.conf` to proxy API calls:

```nginx
location /api/ {
    proxy_pass https://your-backend-service.railway.app/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

## Automatic Deployments

### GitHub Actions Integration

The included workflow (`.github/workflows/ci-cd.yml`) automatically:

1. **Tests** both services on every PR
2. **Builds** Docker images on main/develop branches
3. **Deploys** to Railway when images are ready

### Manual Deployment

```bash
# Deploy backend
cd BackEnd
railway up

# Deploy frontend
cd frontend
railway up
```

## Monitoring and Maintenance

### Health Checks

- **Backend**: `https://your-backend.railway.app/actuator/health`
- **Frontend**: Nginx serves health endpoint automatically

### Logs

```bash
# View backend logs
railway logs --service backend

# View frontend logs
railway logs --service frontend

# Follow logs in real-time
railway logs --follow
```

### Database (if added later)

```bash
# Add PostgreSQL
railway add postgresql

# Get connection string
railway variables
```

## Cost Optimization

### Free Tier Limits
- **$5/month** credit for free accounts
- **500 hours** of usage per month
- **1GB** RAM per service
- **1GB** disk per service

### Optimization Tips
1. **Sleep unused services** automatically
2. **Use minimal resource allocation**
3. **Enable compression** in nginx
4. **Optimize Docker images** with multi-stage builds

## Custom Domain Setup

1. **Add Domain in Railway**
   - Go to service settings
   - Click "Custom Domain"
   - Enter your domain

2. **Update DNS Records**
   ```
   Type: CNAME
   Name: www (or @)
   Value: your-service.railway.app
   ```

3. **SSL Certificate**
   - Automatically provided by Railway
   - Usually ready within 5-10 minutes

## Troubleshooting

### Common Issues

1. **Service Won't Start**
   ```bash
   railway logs --service backend
   ```

2. **API Calls Failing**
   - Check CORS configuration
   - Verify API URL in frontend

3. **Build Failures**
   - Check Dockerfile syntax
   - Verify all dependencies

### Support Resources

- [Railway Documentation](https://docs.railway.app)
- [Railway Discord](https://discord.gg/railway)
- [GitHub Issues](https://github.com/railwayapp/railway/issues)

## Production Checklist

- [ ] Both services deploy successfully
- [ ] Health checks pass
- [ ] API communication works
- [ ] Frontend loads correctly
- [ ] Search and pagination work
- [ ] Sorting functions properly
- [ ] Error handling works
- [ ] Logs are accessible
- [ ] Custom domain configured (optional)
- [ ] SSL certificate active
