
# Maven package
#
echo "================ Compiling ================="

mvn clean package > /dev/null

# Limpio resultados viejos
rm -r ./results/

# Extraigo el tar en target para server y para client
tar -xvf ./client/target/tpe1-g4-client-1.0-SNAPSHOT-bin.tar.gz -C ./client/target > /dev/null
tar -xvf ./server/target/tpe1-g4-server-1.0-SNAPSHOT-bin.tar.gz -C ./server/target > /dev/null

# Chmod para los scripts
chmod +x ./client/target/tpe1-g4-client-1.0-SNAPSHOT/*
chmod +x ./server/target/tpe1-g4-server-1.0-SNAPSHOT/*

export PATH=$PATH:./client/target/tpe1-g4-client-1.0-SNAPSHOT/lib/
export PATH=$PATH:./server/target/tpe1-g4-server-1.0-SNAPSHOT/lib/

# Corro los scripts de tests en test

for file in ./tests/*; do
    # Si no es un shell script, no lo ejecuto
    if [ ! -f $file ]; then
        continue
    fi
    echo "================ Running test $file ================="
    $file
done
