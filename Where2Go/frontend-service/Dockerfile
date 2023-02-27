#FROM node:lts-alpine
#ENV NODE_ENV=production
#WORKDIR /usr/src/app
#COPY ["package.json", "package-lock.json*", "npm-shrinkwrap.json*", "./"]
#RUN npm install --force
#COPY . .
#EXPOSE 4200
#RUN chown -R node /usr/src/app
#USER node
#CMD ["npm", "start"]

# Define a imagem base
FROM node:latest

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos necessários para dentro do contêiner
COPY package*.json ./
COPY . .

# Instala as dependências
RUN npm install -force

# Compila o aplicativo para produção
RUN npm run build --prod


# Expõe a porta 80 para o mundo exterior
EXPOSE 4200

# Inicia o servidor
CMD ["npm", "run", "serve"]

