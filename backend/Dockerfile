FROM node:22

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npx prisma generate

CMD ["sh", "-c", "echo 'DB is up!' && npx prisma migrate deploy && npm run start:dev"]