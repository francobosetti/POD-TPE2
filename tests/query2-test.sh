
# Levanto el server, redirijo la salida al vacio, me quedo con el pid y lo mato al final

echo "Query 2: Levantando server"

cd ./server/target/tpe1-g4-server-1.0-SNAPSHOT
./server.sh -Daddress=127.0.0.1 > /dev/null 2>&1 &

cd ../../..

# Espero a que levante
sleep 5

# Creo el directorio de salida si no existe
mkdir -p ./tests/results

echo "Query 2: Corriendo cliente"

# Query 2
cd ./client/target/tpe1-g4-client-1.0-SNAPSHOT
./query2.sh -DinPath=../../../tests/data/ -DoutPath=../../../tests/results/ -Dcity=NYC -Daddresses=127.0.0.1:5701 > /dev/null 2>&1

cd ../../..

echo "Query 2: Matando server"

# Mato el server
PID=$(ps aux | grep 'ar.edu.itba.pod.server.Server' | grep -v grep | awk '{print $2}')
kill $PID

# Espero a que termine
sleep 5

echo "Query 2: Chequeando resultados"

# Chequeo que el resultado sea el esperado
diff -q ./tests/results/query2.csv ./tests/expected/query2-expected.csv >> /dev/null

if [ $? -eq 0 ]; then
    # Imprimir verde
    echo -e "\e[32mQuery 2: OK, los archivos son iguales\e[0m"
else
    # Imprimir rojo
    echo -e "\e[31mQuery 2: FAIL, los archivos son distintos\e[0m"
fi
