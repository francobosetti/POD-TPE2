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


        // -------- Configuring Hazelcast --------
        final Config config = new Config();

        final GroupConfig groupConfig = new GroupConfig().setName("g4").setPassword("g4-pass");
        config.setGroupConfig(groupConfig);

        final MulticastConfig multicastConfig = new MulticastConfig();
        final JoinConfig joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);

        final Collection<String> interfaces = Collections.singletonList("192.168.0.*");
        final InterfacesConfig interfacesConfig =
                new InterfacesConfig().setInterfaces(interfaces).setEnabled(true);

        final NetworkConfig networkConfig =
                new NetworkConfig().setJoin(joinConfig).setInterfaces(interfacesConfig);

        config.setNetworkConfig(networkConfig);

        // -------- Starting Hazelcast --------

        logger.info("Starting Hazelcast server");
         Hazelcast.newHazelcastInstance(config);
    }
}
