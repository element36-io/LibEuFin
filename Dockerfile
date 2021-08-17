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
RUN ls; which libeufin-nexus; which libeufin-sandbox; which libeufin-cli