FROM zenika/kotlin:1.4.20-M2-jdk11-slim as build

#
#  IMPORTANT - you need to call ./bootstrap in order to inititalize the sub-repo of Libeufin
#  Ohterwise the build will fail with
#  failed to solve: failed to compute cache key: failed to calculate checksum  ..."/build-system/taler-build-scripts/configure": not found
#

RUN apt update
# needed for nexus/sandbox
RUN apt install -y python3 python3-pip git
RUN pip3 install click requests
# needed for starting the UI to use the sandbox - LibFinEu/frontend
RUN apt-get install -y wget curl nodejs yarnpkg npm 
RUN apt-get install postgresql-client -y
#  install versions according to LibFinEu/frontend/README.md
RUN npm install -g n
RUN n 10.16.0
RUN npm install -g npm@6.9.0
RUN npm install --global yarn@1.22.4
RUN yarnpkg global add serve@13.0.4
# moved ahead so that debugging kotlin is faster
RUN mkdir /app; ls -la /app/
COPY gradlew build.gradle gradle.properties Makefile settings.gradle  /app/
COPY ./presentation /app/presentation
COPY ./frontend /app/frontend
WORKDIR /app/
RUN yarn --cwd /app/frontend/ install


# setup system
ARG CACHEBUST=4
COPY ./build-system /app/build-system
COPY ./cli /app/cli
COPY ./contrib /app/contrib
COPY ./debian /app/debian
COPY ./nexus /app/nexus
COPY ./parsing-tests /app/parsing-tests
COPY ./sandbox /app/sandbox
COPY ./util /app/util
COPY ./gradle /app/gradle
COPY build-system/taler-build-scripts/configure /app/configure
# RUN chmod a+x bootstrap; ./bootstrap
RUN chmod a+x configure; ./configure
RUN make all
RUN make install

# RUN yarnpkg --cwd /app/frontend/ build
