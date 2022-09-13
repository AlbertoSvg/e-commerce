#!/bin/sh
set -e

echo " "
echo "Please be sure that env variable JAVA_HOME is set, in order to point to the JDK path"
echo " "
echo " "
if [ -z ${JAVA_HOME+x} ]; then echo "JAVA_HOME is unset"; else echo "JAVA_HOME is set to '$JAVA_HOME'"; fi
echo " "
echo " "
echo "starting gradle tasks..."
echo " "
echo " "
echo "Eureka server"
./gradlew ":Eureka:bootJar"
echo " "
echo "Catalog Service"
./gradlew ":CatalogService:bootJar"
echo " "
echo "Order Service"
./gradlew ":OrderService:bootJar"
echo " "
echo "Wallet Service"
./gradlew ":WalletService:bootJar"
echo " "
echo "Warehouse Service"
./gradlew ":WarehouseService:bootJar"
echo " "
echo " "
echo "Starting docker-compose..."
echo " "
echo " "
docker-compose "down"
docker-compose "build"
docker-compose "up" "-d"
docker "image" "prune" "-f"
docker "volume" "prune" "-f"

echo "Waiting 5 seconds to be sure every container is ready..."
sleep 5

DEBEZIUM_CONFIG_FILE=./debezium_config.json
DEBEZIUM_CONFIG="$(cat "$DEBEZIUM_CONFIG_FILE" | tr -d '\r' | tr -d '\n' | tr -d ' ' )"
echo $DEBEZIUM_CONFIG
echo " "

until output=$(docker exec kafka-connect curl -f kafka-connect:8083/connectors)
  do
    echo "Service is unavailable, sleeping..."
    sleep 5
  done
sleep 2

output=$(echo $output | tr -d '[' | tr -d ']' | tr -d '"')
echo $output
IFS=',' read -a output <<< "$output"

echo " "
echo "Deleting existing connectors..."
echo " "

for connector in ${output[@]}
do
  until docker exec kafka-connect curl -i -f -X DELETE kafka-connect:8083/connectors/"$connector"
    do
      echo "Route is unavailable, sleeping..."
      sleep 5
    done
done
sleep 2

echo " "
echo "Configuring the connector..."
echo " "

until docker exec kafka-connect curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" kafka-connect:8083/connectors/ -d "$DEBEZIUM_CONFIG"
    do
      echo "Route is unavailable, sleeping..."
      sleep 5
    done

sleep 2
echo " "
echo " "
echo " "
echo "Finished."