# ITSM Backend Project

## Docker Local Development Setup

Bu proje Docker Compose kullanılarak lokal ortamda çalıştırılabilir.

---

# Gereksinimler

Bilgisayarında şunlar kurulu olmalı:

- Docker Desktop
- Java 21

---

# .env Dosyası

Proje root dizininde `.env` dosyası bulunmalıdır.

Örnek:

```env
POSTGRES_DB=itsm_db
POSTGRES_USER=elifnisatosun
POSTGRES_PASSWORD=postgres

BACKEND_PORT=8080
POSTGRES_PORT=5432

KEYCLOAK_ISSUER_URI=http://localhost:8081/realms/itsm-realm
KEYCLOAK_JWK_SET_URI=http://host.docker.internal:8081/realms/itsm-realm/protocol/openid-connect/certs

UPLOAD_DIR=./uploads
```

---

# Jar Build Alma

Docker compose çalıştırmadan önce:

```bash
./mvnw clean package -DskipTests
```

komutu çalıştırılmalıdır.

---

# Docker Ortamını Başlatma

Şu komutu çalıştır:

```bash
docker compose up --build -d
```

Bu işlem şunları ayağa kaldırır:

- Spring Boot Backend
- PostgreSQL
- Docker network
- Docker volume

---

# Servis Adresleri

Backend API:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Keycloak:

```text
http://localhost:8081
```

PostgreSQL:

```text
localhost:5432
```

---

# Çalışan Containerları Görme

```bash
docker ps
```

Beklenen containerlar:

- itsm-backend
- itsm-postgres
- keycloak

---

# Sistemi Durdurma

```bash
docker compose down
```

Volume’leri de silmek için:

```bash
docker compose down -v
```

---

# Upload Persistence

Attachment dosyaları şu mount ile saklanır:

```yaml
./uploads:/app/uploads
```

Bu sayede container restart sonrası dosyalar kaybolmaz.

---

# JWT Authentication

Token endpoint:

```text
http://localhost:8081/realms/itsm-realm/protocol/openid-connect/token
```

---

# Faydalı Komutlar

Backend rebuild:

```bash
./mvnw clean package -DskipTests
docker compose down
docker compose up --build -d
```

Backend loglarını görme:

```bash
docker compose logs backend --tail=100
```

Sadece backend restart:

```bash
docker compose restart backend
```