# Transfer service
A projekt egy banki middleware rendszert átutalási szolgálatás funkcióját mutatja be microservices architektúrával.

## Főbb jellemzők
- REST API pénzügyi tranzakciók kezelésére
- PostgreSQL adatbázis a tranzakciók és felhasználói adatok tárolására
- Production-ready Docker és DevOps konfigurációk
- CI/CD pipeline GitLab CI-hez, biztonsági vizsgálatokkal

## Fejlesztői környezet
A projekt a következő technológiákat használja:
- Java 21 (Eclipse Temurin)
- Spring Boot 3.x
- Maven build rendszer
- PostgreSQL 15 adatbázis
- Docker & Docker Compose
- GitLab CI/CD pipeline

## Futtatás Docker Compose-szal (local dev)
A projekthez tartozik egy Docker Compose konfiguráció, amely automatikusan létrehozza a szükséges szolgáltatásokat (alkalmazás, PostgreSQL, Redis).

Klónozd a repository-t: `git clone <repo-url>`\
`cd transfer-service`

Indítsd el a fejlesztői környezetet: `docker-compose up --build`

Az alkalmazás a http://localhost:8080 címen lesz elérhető.\
A PostgreSQL adatbázis:\
Host: localhost:5432\
Database: transfer-db\
User: postgres\
Password: postgres\
A logok a ./logs mappában találhatók a host gépen.

## Docker (production build)
Buildeld a Docker image-et: `docker build -t transfer-service:latest`\
Futtasd a konténert: `docker run -p 8080:8080 transfer-service:latest`

## GitLab CI/CD

A projekt tartalmaz egy .gitlab-ci.yml fájlt, amely a következő pipeline-t biztosítja:
- Build stage: Maven build és artifact létrehozása
- Test stage: Unit és integration tesztek
- Security scan: Trivy Docker image scanning
- Deploy stages: Staging (automatikus) és Production (manuális)