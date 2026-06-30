# MusicRoom

_42 project where you have to make a music streaming service_

## to see the db :

- docker compose exec backend npx prisma studio --port 5555 --browser none

## to update the db with secrets implemented:

- docker compose exec -it backend ./apply*migrations.sh \*\*\_name of new migration*\*\*

## secrets & .env files

- secrets tree:

  ../secrets \
  ├── database_url \
  ├── db_password \
  ├── google_service_account.json \
  ├── jwt_secret \
  ├── oauth_webapp.json \
  └── smtp_password

- root .env:

<<<<<<< HEAD
  DB_NAME= \
  DB_USER= \
  DB_PORT= \
  BACKEND_PORT=
=======
	DB_NAME= \
	DB_USER= \
	DB_PORT= \
	DB_HOST= \
	BACKEND_PORT= \
	EXTERNAL_PORT=
>>>>>>> 73a2577482022b50ab573c49931f0aafc2e18fa0

- backend container .env:

<<<<<<< HEAD
      DOMAIN_NAME= \

  \
   **SMTP Configuration**\
   SMTP_HOST=\
   SMTP_PORT=\
   SMTP_USER=\
   SMTP_FROM=\
  \
   **Google OAuth Configuration**\
   GOOGLE_SCOPES_API=\
   REDIRECT_TO_LOGIN=

redirect URI for google oauth:
https://localhost/auth/oauth/callback
=======
	DOMAIN_NAME= \
\
	**SMTP Configuration**\
	SMTP_HOST=\
	SMTP_PORT=\
	SMTP_USER=\
	SMTP_FROM=\
\
	**Google OAuth Configuration**\
	GOOGLE_SCOPES_API=\
	REDIRECT_TO_LOGIN=
>>>>>>> 73a2577482022b50ab573c49931f0aafc2e18fa0
