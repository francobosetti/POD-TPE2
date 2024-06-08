
# Levanto el server, redirijo la salida al vacio, me quedo con el pid y lo mato al final

echo "Query 4: Levantando server"

cd ./server/target/tpe1-g4-server-1.0-SNAPSHOT
./server.sh > /dev/null 2>&1 &

cd ../../..

# Espero a que levante
sleep 5

# Creo el directorio de salida si no existe
mkdir -p ./tests/results

echo "Query 4: Corriendo cliente"

# Query 1
cd ./client/target/tpe1-g4-client-1.0-SNAPSHOT
./query4.sh -Dfrom='01/01/2019' -Dto='31/12/2021' -DinPath=../../../tests/data/ -DoutPath=../../../tests/results/ -Dcity=NYC -Daddresses=192.168.0.100:5701 > /dev/null 2>&1

cd ../../..

echo "Query 4: Matando server"

# Mato el server
PID=$(ps aux | grep 'ar.edu.itba.pod.server.Server' | grep -v grep | awk '{print $2}')
kill $PID

# Espero a que termine
sleep 5

echo "Query 4: Chequeando resultados"

# Chequeo que el resultado sea el esperado
diff -q ./tests/results/query4.csv ./tests/expected/query4-expected.csv >> /dev/null

if [ $? -eq 0 ]; then
    # Imprimir verde
    echo -e "\e[32mQuery 4: OK, los archivos son iguales\e[0m"
else
    # Imprimir rojo
    echo -e "\e[31mQuery 4: FAIL, los archivos son distintos\e[0m"
fi
