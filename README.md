# MusicRoom

*42 project where you have to make a music streaming service*

## to see the db :
* docker compose exec backend npx prisma studio --port 5555 --browser none

## to update the db with secrets implemented:

* docker compose exec -it backend ./apply_migrations.sh ***name of new migration***

## secrets & .env files

* secrets tree:

	../secrets \
	├── database_url \
	├── db_password \
	├── google_service_account.json \
	├── jwt_secret \
	├── oauth_webapp.json \
	└── smtp_password

* root .env:

	DB_NAME= \
	DB_USER= \
	DB_PORT= \
	DB_HOST= \
	BACKEND_PORT= \
	EXTERNAL_PORT=

* backend container .env:

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
