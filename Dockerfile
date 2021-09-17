FROM zenika/kotlin:1.4.20-M2-jdk11-slim as build
RUN apt update
RUN apt install -y python3 python3-pip git
RUN pip3 install click requests
RUN mkdir /app
COPY ./ /app/
WORKDIR /app/
RUN chmod a+x bootstrap; ./bootstrap
RUN chmod a+x configure; ./configure
RUN make install
# needed for stating the UI to use the sandbox - LibFinEu/frontend
RUN apt-get install -y wget curl nodejs yarnpkg npm 
# needed for init-sandbox script to check if postgres is up
RUN sudo apt-get install postgresql-client  
#  install versions according to LibFinEu/frontend/README.md
RUN npm install -g n
RUN n 10.16.0
RUN npm install -g npm@6.9.0
RUN npm install --global yarn@1.22.4
RUN yarn --cwd /app/frontend/ install
# RUN yarnpkg --cwd /app/frontend/ build
RUN yarnpkg global add serve