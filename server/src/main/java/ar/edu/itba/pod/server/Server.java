package ar.edu.itba.pod.server;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        // -------- Get options --------
        // -Daddress=''
        String address = System.getProperty("address");
        if (address == null) {
            address = "192.168.0.*";
        }

        // -------- Configuring Hazelcast --------
        final Config config = new Config();

        final GroupConfig groupConfig = new GroupConfig().setName("g4").setPassword("g4-pass");
        config.setGroupConfig(groupConfig);

        final MulticastConfig multicastConfig = new MulticastConfig();
        final JoinConfig joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);

        final Collection<String> interfaces = Collections.singletonList(address);
        final InterfacesConfig interfacesConfig =
                new InterfacesConfig().setInterfaces(interfaces).setEnabled(true);

        final NetworkConfig networkConfig =
                new NetworkConfig().setJoin(joinConfig).setInterfaces(interfacesConfig);

        config.setNetworkConfig(networkConfig);

        final MultiMapConfig multiMapConfigQ3 = new MultiMapConfig();
        multiMapConfigQ3.setName("query3");
        multiMapConfigQ3.setBackupCount(0);
        multiMapConfigQ3.setAsyncBackupCount(0);
        multiMapConfigQ3.setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        config.addMultiMapConfig(multiMapConfigQ3);

        final MultiMapConfig multiMapConfig = new MultiMapConfig();
        multiMapConfig.setName("query4");
        multiMapConfig.setBackupCount(0);
        multiMapConfig.setAsyncBackupCount(0);
        multiMapConfig.setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        config.addMultiMapConfig(multiMapConfig);

        final MultiMapConfig multiMapConfigQ5 = new MultiMapConfig();
        multiMapConfigQ5.setName("query5");
        multiMapConfigQ5.setBackupCount(0);
        multiMapConfigQ5.setAsyncBackupCount(0);
        multiMapConfigQ5.setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        config.addMultiMapConfig(multiMapConfigQ5);

        // -------- Starting Hazelcast --------

        logger.info("Starting Hazelcast server");
        Hazelcast.newHazelcastInstance(config);
    }
}
