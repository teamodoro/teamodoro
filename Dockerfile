FROM ubuntu:trusty

RUN apt-get upgrade && sudo apt-get update && apt-get install --no-install-recommends -y openjdk-7-jre-headless
ADD target/universal/stage /home

EXPOSE 9000
