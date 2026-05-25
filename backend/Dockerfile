FROM node:22

WORKDIR /app

RUN chown -R node:node /app && chmod -R 755 /app

COPY package*.json ./

RUN npm install

COPY . .

USER node

# RUN npm run build

CMD [ "npm", "run", "start:dev" ]