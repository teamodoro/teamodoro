FROM bellsoft/liberica-openjre-alpine

RUN apk add --no-cache bash

ADD target/universal/teamodoro-dist.tgz /home/

EXPOSE 9000

CMD /home/teamodoro-dist/bin/teamodoro
