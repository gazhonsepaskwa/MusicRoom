# MusicRoom

*42 project where you have to make a music streaming service*

## to see the db :
* docker compose exec backend npx prisma studio --port 5555 --browser none

## to update the db with secrets implemented:

* docker compose exec -it backend ./apply_migrations.sh ***name of new migration***