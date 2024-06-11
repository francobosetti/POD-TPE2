# POD-TPE2
 
A hazelcast-based project where infraction tickets are loaded in hazelcast cluster and map reduce queries are done


It is required for this project to have <u>java 17</u> installed as JDK.

## Installation

Clone the project and execute the following command:
```bash
mvn clean install
```

In the directories server/target and client/target there will be .tar.gz extension files. You will have to decompress them.
You can do it with the following commands:
```bash
tar -xvf server/target/tpe1-g4-server-1.0-SNAPSHOT-bin.tar.gz
tar -xvf client/target/tpe1-g4-client-1.0-SNAPSHOT-bin.tar.gz
```

After that, you will need to give execution permissions to the scripts inside the directories created from the compressed files

For the Server:
```bash
cd tpe1-g4-server-1.0-SNAPSHOT
chmod u+x ./server.sh
```

For the Clients:
```bash
cd tpe1-g4-client-1.0-SNAPSHOT
chmod u+x ./query1.sh
chmod u+x ./query2.sh
chmod u+x ./query3.sh
chmod u+x ./query4.sh
chmod u+x ./query5.sh
```

## Use

Now everything is ready to use

### Server

First we have to enter the folder tpe1-g4-server-1.0-SNAPSHOT

Then we can run the server by executing the following command:
```bash
./server.sh [-Daddress=xx.xx.xx.xx]
```

- _Parameters_
 - _Daddress (optional)_: The interface used in the config, defaults to `192.168.0.*`

### Clients

First we have to enter the folder tpe1-g4-client-1.0-SNAPSHOT

Then we can run the clients executing the following commands:

#### Query 1: Total tickets per infraction
```bash
./query1.sh -Daddresses='xx.xx.xx.xx:yyyy;ww.ww.ww.ww:zzzz' -Dcity=CHI|NYC -DinPath=datasetPath -DoutPath=outPath [-DmaxRecords=maxRecords -Dcombiner=true|false]
```

- _Parameters_
 - _Daddresses_: A list of addresses of the cluster nodes
 - _Dcity_: The city to get the data from, the 2 supported cities are CHI or NYC
 - _DinPath_: Path of the directory containing the dataset
 - _DoutPath_: Path of the directory to put the query results
 - _DmaxRecords (optional)_: Maximum number of entries to read from the tickets data file, if not set reads the entire file
 - _Dcombiner (optional)_: Indicates if a combiner will be used, if not set defaults to true

#### Query 2: Top 3 most common infractions in each area
```bash
./query2.sh -Daddresses='xx.xx.xx.xx:yyyy;ww.ww.ww.ww:zzzz' -Dcity=CHI|NYC -DinPath=datasetPath -DoutPath=outPath [-DmaxRecords=maxRecords -Dcombiner=true|false]
```

- _Parameters_
 - _Daddresses_: A list of addresses of the cluster nodes
 - _Dcity_: The city to get the data from, the 2 supported cities are CHI or NYC
 - _DinPath_: Path of the directory containing the dataset
 - _DoutPath_: Path of the directory to put the query results
 - _DmaxRecords (optional)_: Maximum number of entries to read from the tickets data file, if not set reads the entire file
 - _Dcombiner (optional)_: Indicates if a combiner will be used, if not set defaults to true

#### Query 3: Top N agencies with the highest revenue percentage
```bash
./query3.sh -Daddresses='xx.xx.xx.xx:yyyy;ww.ww.ww.ww:zzzz' -Dcity=CHI|NYC -DinPath=datasetPath -DoutPath=outPath -Dn=n [-DmaxRecords=maxRecords]
```
- _Parameters_
 - _Daddresses_: A list of addresses of the cluster nodes
 - _Dcity_: The city to get the data from, the 2 supported cities are CHI or NYC
 - _DinPath_: Path of the directory containing the dataset
 - _DoutPath_: Path of the directory to put the query results
 - _Dn_: Number of agencies to show
 - _DmaxRecords (optional)_: Maximum number of entries to read from the tickets data file, if not set reads the entire file


#### Query 4: License plate with the most infractions in each area within the range [from, to]
```bash
./query4.sh -Daddresses='xx.xx.xx.xx:yyyy;ww.ww.ww.ww:zzzz' -Dcity=CHI|NYC -DinPath=datasetPath -DoutPath=outPath -Dfrom='DD/MM/YY' -Dto='DD/MM/YY' [-DmaxRecords=maxRecords]
```

- _Parameters_
 - _Daddresses_: A list of addresses of the cluster nodes
 - _Dcity_: The city to get the data from, the 2 supported cities are CHI or NYC
 - _DinPath_: Path of the directory containing the dataset
 - _DoutPath_: Path of the directory to put the query results
 - _Dfrom_: Date where the range starts
 - _Dto_: Date where the range ends
 - _DmaxRecords (optional)_: Maximum number of entries to read from the tickets data file, if not set reads the entire file

#### Query 5: Pairs of infractions that have, in groups of hundreds, the same average fine amount
```bash
./query5.sh -Daddresses='xx.xx.xx.xx:yyyy;ww.ww.ww.ww:zzzz' -Dcity=CHI|NYC -DinPath=datasetPath -DoutPath=outPath [-DmaxRecords=maxRecords]
```
- _Parameters_
 - _Daddresses_: A list of addresses of the cluster nodes
 - _Dcity_: The city to get the data from, the 2 supported cities are CHI or NYC
 - _DinPath_: Path of the directory containing the dataset
 - _DoutPath_: Path of the directory to put the query results
 - _DmaxRecords (optional)_: Maximum number of entries to read from the tickets data file, if not set reads the entire file

## Tests

We also provided a script for testing the queries:

To run this test, execute the following command:
```bash
./tests.sh
```
