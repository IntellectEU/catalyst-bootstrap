package com.intellecteu.catalyst.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Accordance between use cases and plugins needed
 *
 * @author Taras Shvyryd
 */
public class PluginBinding {

    private static Map<String, String[]> binding = new HashMap<>();

    static {
        binding.put("soapconnector", new String[]{"build-helper-maven-plugin", "jaxws-maven-plugin"});
    }

    /**
     * @param useCase id in the application.yml of catalyst-service
     * @return plugin file names
     */
    public static String[] getPlugins(String useCase){
        return binding.get(useCase);
    }
}
