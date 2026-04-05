# POC — Chat support Your Car Your Way

Preuve de concept de la fonctionnalité de chat en temps réel avec support, dans le cadre du projet de refonte de l'application Your Car Your Way.

---

## Contexte

Ce POC démontre la faisabilité technique des choix architecturaux retenus pour la nouvelle plateforme Your Car Your Way, et plus particulièrement :

- L'authentification JWT avec gestion des rôles (CLIENT / SUPPORT)
- Le chat en temps réel via WebSocket (STOMP)
- La gestion d'une file d'attente de conversations
- La persistance des messages en base de données PostgreSQL

---

## Stack technique

| Couche | Technologie |
|---|---|
| Backend | Spring Boot 3 (Java 21) |
| Frontend | Angular 17 |
| Base de données | PostgreSQL 16 (Docker) |
| Temps réel | Spring WebSocket (STOMP) |
| Authentification | JWT + Spring Security |
| Migrations BDD | Flyway |

---

## Prérequis

- [Docker](https://www.docker.com/) et Docker Compose
- [Java 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Node.js 20+](https://nodejs.org/) et npm
- [Angular CLI](https://angular.io/cli) : `npm install -g @angular/cli`

---

## Lancer le projet

### 1. Démarrer la base de données

```bash
docker-compose up -d
```

PostgreSQL démarre sur le port `5432`. Les tables et données de test sont créées automatiquement par Flyway au premier démarrage de Spring Boot.

### 2. Démarrer le backend

```bash
cd backend
mvn spring-boot:run
```

Le backend démarre sur `http://localhost:8080`.

### 3. Démarrer le frontend

```bash
cd frontend
npm install
ng serve
```

Le frontend est accessible sur `http://localhost:4200`.

---

## Comptes de test

| Email | Mot de passe | Rôle |
|---|---|---|
| `client@test.com` | `password123` | CLIENT |
| `agent@test.com` | `password123` | SUPPORT |

---

## Scénario de test

1. Ouvrir `http://localhost:4200` dans **deux onglets** (ou deux navigateurs)
2. Dans le **premier onglet** : se connecter avec `client@test.com`
3. Cliquer sur **"Contacter le support"** pour ouvrir une conversation
4. Dans le **second onglet** : se connecter avec `agent@test.com`
5. L'agent voit la conversation apparaître dans sa **file d'attente**
6. L'agent clique sur **"Prendre en charge"**
7. Les deux utilisateurs peuvent **échanger des messages en temps réel**
8. L'agent clique sur **"Fermer la conversation"** pour terminer

---

## Structure du projet

```
.
├── docker-compose.yml
├── README.md
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/yourcaryourway/poc/
│       │   │   ├── config/
│       │   │   ├── auth/
│       │   │   ├── chat/
│       │   │   └── model/
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/
│       │           ├── V1__create_schema.sql
│       │           └── V2__insert_test_data.sql
└── frontend/
    └── src/app/
        ├── core/
        ├── features/
        └── shared/
```

---

## API REST

### Authentification

| Méthode | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Créer un compte | Non |
| POST | `/api/auth/login` | Se connecter | Non |

### Chat

| Méthode | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/conversations` | Ouvrir une conversation | CLIENT |
| GET | `/api/conversations/queue` | Voir la file d'attente | SUPPORT |
| PATCH | `/api/conversations/{id}/take` | Prendre en charge | SUPPORT |
| PATCH | `/api/conversations/{id}/close` | Fermer la conversation | SUPPORT |
| GET | `/api/conversations/{id}/messages` | Historique des messages | CLIENT / SUPPORT |

### WebSocket

| Topic | Description |
|---|---|
| `/app/chat/{conversationId}` | Envoyer un message |
| `/topic/conversation/{conversationId}` | Recevoir les messages |
| `/topic/queue` | Notifier les agents d'une nouvelle conversation |

---

## Base de données

La base est initialisée automatiquement par Flyway au démarrage. Pour vérifier les données :

```bash
# Se connecter à la base
docker exec -it ycyw_poc_db psql -U ycyw -d ycyw_poc

# Vérifier les tables
\dt

# Vérifier les conversations
SELECT * FROM conversation;

# Vérifier les messages
SELECT * FROM chat_message;
```

---

## Arrêter le projet

```bash
# Arrêter la base de données
docker-compose down

# Arrêter et supprimer les données
docker-compose down -v
```