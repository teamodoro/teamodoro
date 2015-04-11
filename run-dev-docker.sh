#!/bin/sh

cmd=(
  docker run 
    -e TEAMODORO_DB_URL=jdbc:h2:/data/teamodoro
    -p 0.0.0.0:9000:9000  
    -it 
    -v `pwd`:/home 
    teamodoro-dev.1 bash
) 

"${cmd[@]}"
