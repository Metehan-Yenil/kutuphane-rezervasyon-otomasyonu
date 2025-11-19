# 🐳 Docker Deployment Guide

## 📋 Gereksinimler
- Docker Desktop (Windows/Mac) veya Docker Engine (Linux)
- Docker Compose v2.0+

## 🚀 Hızlı Başlangıç

### 1. Development Ortamı

```bash
# Docker container'ları başlat
docker-compose up -d

# Logları takip et
docker-compose logs -f

# Durumu kontrol et
docker-compose ps
```

**Backend:** http://localhost:8080  
**PostgreSQL:** localhost:5432

### 2. Production Ortamı

```bash
# .env dosyasını oluştur
cp .env.example .env

# .env dosyasını düzenle (şifreleri güncelle!)
nano .env

# Production ortamını başlat
docker-compose -f docker-compose.prod.yml up -d

# Logları kontrol et
docker-compose -f docker-compose.prod.yml logs -f backend
```

## 🔧 Komutlar

### Container Yönetimi

```bash
# Tüm servisleri başlat
docker-compose up -d

# Sadece backend'i yeniden başlat
docker-compose restart backend

# Sadece database'i başlat
docker-compose up -d postgres

# Tüm servisleri durdur
docker-compose down

# Servisleri durdur ve volume'leri sil (DİKKAT: Veri kaybı!)
docker-compose down -v
```

### Image Yönetimi

```bash
# Backend image'ını yeniden build et
docker-compose build backend

# Cache kullanmadan build et
docker-compose build --no-cache backend

# Tüm servisleri yeniden build et ve başlat
docker-compose up -d --build
```

### Log ve Debug

```bash
# Tüm servislerin loglarını göster
docker-compose logs

# Sadece backend logları (son 100 satır)
docker-compose logs --tail=100 backend

# Real-time log takibi
docker-compose logs -f backend

# Container içine gir
docker exec -it kutuphane-backend bash

# PostgreSQL'e bağlan
docker exec -it kutuphane-postgres psql -U kutuphane_user -d kutuphane_db
```

### Database İşlemleri

```bash
# Database backup
docker exec kutuphane-postgres pg_dump -U kutuphane_user kutuphane_db > backup.sql

# Database restore
cat backup.sql | docker exec -i kutuphane-postgres psql -U kutuphane_user -d kutuphane_db

# PostgreSQL shell
docker exec -it kutuphane-postgres psql -U kutuphane_user -d kutuphane_db
```

## 🔍 Health Check

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Container durumu
docker-compose ps

# Container health status
docker inspect kutuphane-backend --format='{{.State.Health.Status}}'
```

## 🛠️ Sorun Giderme

### Backend başlamıyor
```bash
# Logları kontrol et
docker-compose logs backend

# Container'ı yeniden başlat
docker-compose restart backend

# Image'ı yeniden build et
docker-compose up -d --build backend
```

### Database bağlantı hatası
```bash
# PostgreSQL durumunu kontrol et
docker-compose ps postgres

# PostgreSQL loglarını kontrol et
docker-compose logs postgres

# PostgreSQL health check
docker exec kutuphane-postgres pg_isready -U kutuphane_user -d kutuphane_db
```

### Port çakışması
```bash
# Kullanılan portları kontrol et
netstat -ano | findstr :8080
netstat -ano | findstr :5432

# .env dosyasında portları değiştir
APP_PORT=8081
DB_PORT=5433
```

## 📊 Monitoring

### Container Resources
```bash
# CPU, Memory kullanımı
docker stats

# Sadece backend
docker stats kutuphane-backend
```

### Disk Kullanımı
```bash
# Docker disk kullanımı
docker system df

# Volume boyutları
docker volume ls
docker volume inspect kutuphane_postgres_data
```

## 🔄 Güncelleme

```bash
# Kod değişikliklerini uygula
git pull
docker-compose up -d --build backend

# Sadece image'ı güncelle
docker-compose build backend
docker-compose up -d backend
```

## 🧹 Temizlik

```bash
# Kullanılmayan container'ları temizle
docker container prune

# Kullanılmayan image'ları temizle
docker image prune -a

# Kullanılmayan volume'leri temizle (DİKKAT!)
docker volume prune

# Tüm Docker cache'i temizle
docker system prune -a
```

## 🔐 Güvenlik

### Production için öneriler:
1. `.env` dosyasını asla git'e commit etmeyin
2. Güçlü database şifreleri kullanın
3. CORS ayarlarını production URL'lere göre yapılandırın
4. SSL/TLS kullanın (nginx reverse proxy)
5. Database backup'larını düzenli alın

## 🌐 Nginx Reverse Proxy (Opsiyonel)

Frontend ve backend için tek domain kullanmak isterseniz:

```nginx
# /etc/nginx/sites-available/kutuphane
server {
    listen 80;
    server_name kutuphane.example.com;

    # Backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Frontend
    location / {
        proxy_pass http://localhost:4200;
        proxy_set_header Host $host;
    }
}
```

## 📞 Destek

Sorun yaşarsanız:
1. Logları kontrol edin: `docker-compose logs -f`
2. Container durumunu kontrol edin: `docker-compose ps`
3. Health check yapın: `curl http://localhost:8080/actuator/health`
