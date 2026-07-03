# MusicRoom

_42 project where you have to make a music streaming service_

## to see the db :

- adminer is exposed in port 1919

## to update the db with secrets implemented:

- docker compose exec -it backend ./apply*migrations.sh \*\*\_name of new migration*\*\*

## secrets & .env files

- **secrets tree:**

	secrets								\
	├── database_url					\
	├── db_password						\
	├── firebase-service-account.json	\
	├── google_service_account.json		\
	├── jwt_secret						\
	├── oauth_webapp.json				\
	├── smtp_password					\
	├── spotify_user_id					\
	└── spotify_user_secret	

- **root .env:**

	DOMAIN_NAME= \

	DB_NAME= \
	DB_USER= \
	DB_PORT= \
	DB_HOST= \
	BACKEND_PORT= \
	EXTERNAL_PORT= \
	CADDY_INTERNAL_PORT= \
	CADDY_DOMAIN_NAME= \
\
	***SMTP Configuration***\
	SMTP_HOST=\
	SMTP_PORT=\
	SMTP_USER=\
	SMTP_FROM=\
\
	***Google OAuth Configuration***\
	GOOGLE_SCOPES_API=\
	REDIRECT_TO_LOGIN=
