all :
	docker compose up --build

down:
	docker compose down -v

re: down
	docker compose build --no-cache && \
	docker compose up -d

visu:
	docker compose --profile visual up