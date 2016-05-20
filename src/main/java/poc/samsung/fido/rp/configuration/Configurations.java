package poc.samsung.fido.rp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("configurations")
@Configuration
@PropertySource("classpath:application.properties")
public class Configurations {

    private static final String OXYGEN_API_URI_PREFIX = "oxygen.api.uri.";

    private static final String OXYGEN_API_KEY_PREFIX = "oxygen.api.key.";

    private static final String OXYGEN_API_MESSAGE_LIFETIMEMILLIS = "oxygen.api.message.lifetimeMillis";

    private static final int DEFAULT_OXYGEN_API_MESSAGE_LIFETIMEMILLIS = 30000;
    
    @Autowired
	private Environment env;
    
    @Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

    public String getApiKey(String rpId) {
        return env.getProperty(OXYGEN_API_KEY_PREFIX + rpId).replaceAll("\"", "");
    }

    public String getApiUri(String rpId) {
        return env.getProperty(OXYGEN_API_URI_PREFIX + rpId).replaceAll("\"", "");
    }

    public int getLifetimeMillis() {
        return Integer.valueOf(env.getProperty(OXYGEN_API_MESSAGE_LIFETIMEMILLIS, String.valueOf(DEFAULT_OXYGEN_API_MESSAGE_LIFETIMEMILLIS)));
    }
}
