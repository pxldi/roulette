## Start Application Without Docker

WSL 

Go to directory with `build.sbt`

Start Frontend and Backend:

`sbt "runMain Roulette.controller.ControllerApi`

`sbt "runMain TuiClient"`

Place a Bet:

`bet 1 e e 13`

`d`


## API Endpoints
```bash
curl -X POST -i http://localhost:8085/roulette/setupPlayers
curl -X GET http://localhost:8080/roulette/getResult
curl -X POST http://localhost:8080/roulette/save
curl -X POST http://localhost:8080/roulette/load
curl -X POST http://localhost:8080/roulette/saveDB
curl -X POST http://localhost:8080/roulette/loadDB
curl -X POST http://localhost:8080/roulette/undo
curl -X POST http://localhost:8080/roulette/redo
```



## Docker Swarm

Go to Directory /swarmDev

### Init New Docker Swarm Stack
`docker swarm init`

### Create New Docker Images
#### Backend:

`docker-compose -f ./backendDeployment.yaml build`

`docker push nielshen/roulette:backend-latest`

#### Frontend:

`docker-compose -f ./frontendDeployment.yaml build`

`docker push nielshen/roulette:frontend-latest`

### Apply New Docker Images
`docker stack deploy -c docker-compose.yml meinStack`

### Show Pods Inside Docker-Swarm
`docker service ls`

`docker stack services meinStack`

### Remove Docker-Swarm Stack
`docker stack rm meinStack`



## Test Frontend, Database, Kafka Inside Docker

#### Frontend
`docker attach <container id Frontend>`

`bet 1 e e 13`

`d`

#### PostgreSQL:
use db-client container:
`docker exec -it <container ID> bash`

`psql -h postgres -U poldi -d roulette`

`Password: password`

#### MongoDB:
use db-client container:
`docker exec -it <container ID> bash`

(mongo mongodb://mongo:27017/roulette)

`mongo mongodb://mongo:27017`

`show collections;`

`db.test.insert({name: "test"});`

`db.test.find();`

#### Kafka:
docker attach [container id kafka container]

`kafka-console-producer.sh --broker-list kafka:9092 --topic test`

send message e.g "test"
