FROM node:22

WORKDIR /app

COPY package*.json ./
RUN export DATABASE_URL="$(cat /run/secrets/database_url)"

RUN npm install

COPY . .

RUN ls -la

ENTRYPOINT ["sh", "./conf/launch.sh"]
