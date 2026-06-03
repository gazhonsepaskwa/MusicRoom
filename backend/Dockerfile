FROM node:22

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN ls -la

ENTRYPOINT ["sh", "./conf/launch.sh"]
