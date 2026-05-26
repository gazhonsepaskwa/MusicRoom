all: up

re:
	docker compose down -v && \
	docker compose build --no-cache && \
	docker compose up -d

rebuild:
	docker compose down -v && \
	docker compose build --no-cache

up:
	docker compose up --build

down:
	docker compose down

logs:
	docker compose logs -f

status:
	docker compose ps

visu:
	docker compose --profile visual up
