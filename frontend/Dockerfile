# build environment
FROM node:10.20.0 as build
WORKDIR /webapp
ENV PATH /webapp/node_modules/.bin:$PATH
COPY package.json .
RUN npm i --silent
COPY . ./
RUN npm run build

# production environment
FROM nginx:stable-alpine
COPY --from=build /webapp/build /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 8081
CMD ["nginx", "-g", "daemon off;"]