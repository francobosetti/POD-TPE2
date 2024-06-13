package ar.edu.itba.pod.client.utils;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastUtils {

    private static final String GROUP_NAME = "g4";
    private static final String GROUP_PASSWORD = "g4-pass";

    public static HazelcastInstance getHazelcastInstance(String addressesString) {
        ClientConfig clientConfig = new ClientConfig();

        GroupConfig groupConfig = new GroupConfig().setName(GROUP_NAME).setPassword(GROUP_PASSWORD);
        clientConfig.setGroupConfig(groupConfig);

        ClientNetworkConfig networkConfig = new ClientNetworkConfig();
        networkConfig.addAddress(getAddresses(addressesString));
        clientConfig.setNetworkConfig(networkConfig);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    private static String[] getAddresses(String addressesString) {
        String[] addresses = addressesString.split(";");

        // Sanitize addresses
        for (int i = 0; i < addresses.length; i++) {
            addresses[i] = addresses[i].trim();
        }

        // Check if addresses are valid
        String addressRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}";
        for (String address : addresses) {
            if (!address.matches(addressRegex)) {
                throw new IllegalArgumentException("Invalid address: " + address);
            }
        }

        return addresses;
    }
}
