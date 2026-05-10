# Enterprise ITSM Backend Sistemi

## Proje Genel Bakış

Bu proje, Spring Boot kullanılarak geliştirilmiş production odaklı kurumsal bir IT Service Management (ITSM) backend platformudur.

Sistem uçtan uca ticket yaşam döngüsü yönetimi sağlar:

* Authentication & Authorization
* Ticket yönetimi
* Workflow takibi
* SLA izleme
* Audit log sistemi
* Attachment yönetimi
* Worklog takibi
* Reporting API’leri
* Notification altyapısı

Proje enterprise backend mimarisi hedeflenerek geliştirilmiştir.

---

# Kullanılan Teknolojiler

## Backend

* Java 21
* Spring Boot 3.3.x
* Spring Security
* Spring Data JPA
* Hibernate
* Maven

## Authentication & Security

* Keycloak
* OAuth2 Resource Server
* JWT Authentication
* Role-Based Access Control (RBAC)

## Database

* PostgreSQL
* Flyway Migration

## API Documentation

* Swagger / OpenAPI

## DevOps & Infrastructure

* Docker
* Docker Compose

---

# Mimari Yapı

Proje ölçeklenebilirlik ve gelecekte microservice mimarisine geçiş düşünülerek package-by-feature yaklaşımı ile geliştirilmiştir.

Ana modüller:

* auth
* user
* ticket
* comment
* attachment
* workflow
* sla
* reporting
* notification
* audit
* category
* common
* security
* exception
* config

---

# Roller

Sistem aşağıdaki roller üzerine kuruludur:

## CUSTOMER

* Ticket oluşturabilir
* Kendi ticketlarını görüntüleyebilir
* Ticket yorumlarını görebilir
* Ticket kapatabilir veya reopen edebilir

## AGENT

* Ticket claim edebilir
* Ticket çözüm sürecini yönetebilir
* Internal comment ekleyebilir
* Worklog girebilir
* Attachment yükleyebilir

## MANAGER

* Ticket assign edebilir
* SLA süreçlerini yönetebilir
* Reporting endpointlerine erişebilir
* Audit kayıtlarını görüntüleyebilir

## ADMIN

* Kullanıcı ve rol yönetimi yapabilir
* Tüm sistem endpointlerine erişebilir
* Workflow ve SLA yönetimini kontrol edebilir

---

# Ticket Lifecycle

Sistem aşağıdaki ticket yaşam döngüsünü kullanır:

```text
NEW → TRIAGE → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
```

Desteklenen işlemler:

* Ticket oluşturma
* Ticket assign
* Agent claim
* Status update
* Reopen
* Close
* Internal comment
* Workflow history
* SLA tracking

---

# Özellikler

## Authentication & Authorization

* JWT tabanlı güvenlik
* Keycloak entegrasyonu
* Role-based authorization
* Resource-level security

## Ticket Management

* Ticket oluşturma
* Ticket listeleme
* Ticket filtreleme
* Pagination & sorting
* Priority yönetimi
* Category yönetimi

## Comment System

* Internal comment desteği
* Customer-visible comment desteği
* Comment audit takibi

## Attachment System

* Multipart file upload
* Dosya indirme
* Metadata yönetimi
* Upload persistence

## Worklog System

* Ticket bazlı zaman takibi
* Agent worklog girişleri
* Effort tracking

## SLA System

* SLA policy yönetimi
* SLA tracking
* Due date hesaplama
* Breach kontrolü
* Scheduled SLA monitoring

## Workflow System

* Workflow transition history
* State tracking
* Transition audit kayıtları

## Reporting

* Ticket summary raporları
* SLA compliance raporları
* Trend analizleri
* Agent performance raporları

## Audit Logging

* Sistem audit kayıtları
* User action tracking
* Compliance destek yapısı

## Notification System

* Kullanıcı notification altyapısı
* Read/unread yönetimi

---

# Lokal Ortamda Çalıştırma

## Gereksinimler

Bilgisayarında şunlar kurulu olmalıdır:

* Docker Desktop
* Java 21

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

# Docker Ortamını Başlatma

```bash
./mvnw clean package -DskipTests
```

Sonrasında:

```bash
docker compose up --build -d
```

Bu işlem aşağıdaki servisleri ayağa kaldırır:

* Spring Boot Backend
* PostgreSQL
* Keycloak
* Docker network
* Docker volume

---

# Servis Adresleri

## Backend API

```text
http://localhost:8080
```

## Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

## Keycloak

```text
http://localhost:8081
```

## PostgreSQL

```text
localhost:5432
```

---

# Swagger / OpenAPI

Tüm endpointler Swagger/OpenAPI ile dokümante edilmiştir.

Swagger UI üzerinden:

* Endpoint testleri
* Request/response inceleme
* Authentication testleri
* Workflow endpointleri
* SLA endpointleri
* Reporting endpointleri

kolayca incelenebilir.

---

# Keycloak Yapılandırması

## Realm

```text
itsm-realm
```

## Roller

* ADMIN
* MANAGER
* AGENT
* CUSTOMER

---

# Faydalı Docker Komutları

## Çalışan Containerları Görme

```bash
docker ps
```

Beklenen containerlar:

* itsm-backend
* itsm-postgres
* keycloak

---

## Sistemi Durdurma

```bash
docker compose down
```

Volume’leri de silmek için:

```bash
docker compose down -v
```

---

## Backend Rebuild

```bash
./mvnw clean package -DskipTests
docker compose down
docker compose up --build -d
```

---

## Backend Loglarını Görme

```bash
docker compose logs backend --tail=100
```

---

# Upload Persistence

Attachment dosyaları aşağıdaki mount ile saklanır:

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

# Demo Akışı

## CUSTOMER

* Login olur
* Ticket oluşturur

## AGENT

* Ticket claim eder
* Internal comment ekler
* Attachment yükler
* Worklog girer
* Ticket resolve eder

## CUSTOMER

* Ticket reopen eder veya kapatır

## MANAGER / ADMIN

* SLA tracking görüntüler
* Reporting endpointlerini kullanır
* Audit logları inceler
* Workflow history görüntüler

---

# Gelecek Geliştirmeler

İleride eklenebilecek enterprise geliştirmeler:

* OpenSearch entegrasyonu
* Kafka event streaming
* Email notification sistemi
* Redis cache
* CI/CD pipeline
* Kubernetes deployment
* Microservice architecture
* Distributed tracing
* Centralized logging
* Monitoring & observability

---

# Build Durumu

Son doğrulamalar:

```text
./mvnw clean compile → BUILD SUCCESS
./mvnw clean test → BUILD SUCCESS
```

Sistem:

* PostgreSQL bağlantısı başarılı
* Flyway migration başarılı
* Keycloak entegrasyonu başarılı
* Swagger/OpenAPI aktif
* Docker containerları sağlıklı
* Scheduler görevleri çalışıyor

---

# Lisans

Bu proje eğitim ve portföy amaçlı geliştirilmiştir.
