FROM maven:3-jdk-13
COPY ./ /usr/share/myservice
WORKDIR /usr/share/myservice/
RUN mvn -V package
