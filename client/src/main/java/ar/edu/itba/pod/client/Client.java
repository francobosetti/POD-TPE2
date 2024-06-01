package ar.edu.itba.pod.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("tpe1-g4 Client Starting ...");


        // -------- Configuring Hazelcast --------
        ClientConfig clientConfig = new ClientConfig();

        GroupConfig groupConfig = new GroupConfig().setName("g4").setPassword("g4-pass");
        clientConfig.setGroupConfig(groupConfig);

        ClientNetworkConfig networkConfig = new ClientNetworkConfig();
        String[] addresses = {"192.168.0.100:5701"};
        networkConfig.addAddress(addresses);
        clientConfig.setNetworkConfig(networkConfig);

        // -------- Starting Hazelcast --------
        logger.info("Starting Hazelcast client");
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        String mapName = "map";

        IMap<String, String> map = client.getMap(mapName);
        map.set("key", "value");

        map = client.getMap(mapName);

        logger.info("Value for key: " + map.get("key"));

        logger.info("tpe1-g4 Client Finished");
    }
}
