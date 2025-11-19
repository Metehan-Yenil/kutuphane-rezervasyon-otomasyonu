# Railway Configuration for Backend

## Service: kutuphane-backend

### Build Settings
- **Builder:** Dockerfile
- **Dockerfile Path:** ./Dockerfile
- **Build Command:** (automatic via Docker)
- **Root Directory:** /

### Environment Variables
Add these in Railway Dashboard:

```env
# Spring Boot Profile
SPRING_PROFILES_ACTIVE=prod

# Database (Railway PostgreSQL Plugin)
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${PGUSER}
SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

# JPA/Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# Server
SERVER_PORT=8080

# CORS (Frontend Railway URL)
CORS_ALLOWED_ORIGINS=https://kutuphane-frontend.up.railway.app,https://your-custom-domain.com

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_KUTUPHANEREZERVASYON=DEBUG

# Security (Change in production!)
JWT_SECRET=${JWT_SECRET:-your-secret-key-change-this-in-production}
JWT_EXPIRATION=86400000
```

### Deploy Settings
- **Start Command:** (automatic via Docker ENTRYPOINT)
- **Health Check Path:** /actuator/health
- **Health Check Timeout:** 300 seconds

### Networking
- **Port:** 8080 (internal)
- **Public Domain:** Auto-generated or custom domain

### Resources
- **Memory:** 1024 MB (recommended)
- **CPU:** 1 vCPU (recommended)

## Database Setup (PostgreSQL)

### 1. Add PostgreSQL Plugin
1. Go to Railway project
2. Click "New" → "Database" → "Add PostgreSQL"
3. Railway automatically creates and links database

### 2. Database Variables (Auto-generated)
Railway provides these automatically:
- `DATABASE_URL`
- `PGHOST`
- `PGPORT`
- `PGDATABASE`
- `PGUSER`
- `PGPASSWORD`

## Setup Instructions

### 1. Install Railway CLI
```bash
npm install -g @railway/cli
```

### 2. Login to Railway
```bash
railway login
```

### 3. Initialize Project
```bash
cd D:\kutuphane
railway init
```

### 4. Link to Service
```bash
railway link
```

### 5. Add PostgreSQL
```bash
railway add --database postgresql
```

### 6. Set Environment Variables
```bash
railway variables set SPRING_PROFILES_ACTIVE=prod
railway variables set CORS_ALLOWED_ORIGINS=https://your-frontend.up.railway.app
railway variables set JWT_SECRET=$(openssl rand -base64 32)
```

### 7. Deploy
```bash
railway up
```

## Application Properties for Railway

Create `src/main/resources/application-prod.properties`:

```properties
# Server
server.port=${SERVER_PORT:8080}

# Database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${PGUSER}
spring.datasource.password=${PGPASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# Logging
logging.level.root=INFO
logging.level.com.kutuphanerezervasyon=DEBUG

# CORS
cors.allowed.origins=${CORS_ALLOWED_ORIGINS:http://localhost:4201}

# Security
jwt.secret=${JWT_SECRET:default-secret-key}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

## Automatic Deployment

After connecting to GitHub:
1. Push to `main` branch
2. Railway automatically builds Docker image
3. Runs database migrations (if configured)
4. Deploys to production
5. Assigns a public URL

## Monitoring

### View Logs
```bash
railway logs --tail 100
```

### View Metrics
- Go to Railway Dashboard
- Select backend service
- View CPU, Memory, Network metrics

### Health Check
```bash
curl https://your-backend.up.railway.app/actuator/health
```

## Database Management

### Connect to PostgreSQL
```bash
railway connect postgres
```

### Run SQL Queries
```bash
railway run psql $DATABASE_URL
```

### Backup Database
```bash
railway run pg_dump $DATABASE_URL > backup.sql
```

### Restore Database
```bash
cat backup.sql | railway run psql $DATABASE_URL
```

## Troubleshooting

### Build fails
- Check `pom.xml` for dependency issues
- Verify Java version (21)
- Check Railway build logs

### Database connection fails
- Verify PostgreSQL plugin is added
- Check `DATABASE_URL` environment variable
- Ensure correct JDBC driver

### App crashes after deployment
```bash
# View detailed logs
railway logs --tail 200

# Check environment variables
railway variables

# Restart service
railway restart
```

### High memory usage
- Increase memory limit in Railway
- Optimize JVM settings in Dockerfile:
```dockerfile
ENTRYPOINT ["java", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-jar", "app.jar"]
```

## Performance Optimization

### 1. Enable Connection Pooling
Already configured in `application-prod.properties`

### 2. Add Redis Cache (Optional)
```bash
railway add --database redis
```

### 3. Configure Actuator Endpoints
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

## Security Best Practices

1. ✅ Use strong JWT secret
2. ✅ Enable HTTPS (automatic on Railway)
3. ✅ Set proper CORS origins
4. ✅ Don't commit secrets to Git
5. ✅ Use environment variables
6. ✅ Enable SQL injection protection (JPA)
7. ✅ Implement rate limiting
8. ✅ Regular security updates

## Cost Optimization

- **Starter Plan:** $5/month (includes 500 hours)
- **Database:** Included in plan
- **Bandwidth:** 100 GB included
- **Sleep unused services:** Save credits

## Custom Domain

1. Railway Dashboard → Backend Service
2. Settings → Domains
3. Add domain: `api.kutuphane.com`
4. Update DNS:
   ```
   CNAME @ your-backend.up.railway.app
   ```

## CI/CD Integration

GitHub Actions automatically:
1. Runs tests
2. Builds Docker image
3. Pushes to Docker Hub
4. Triggers Railway deployment
5. Runs load tests (Gatling)
6. Sends notifications
