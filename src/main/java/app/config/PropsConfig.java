package app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AdminSeedProperties.class,
        UsersSeedProperties.class,
        MinioProperties.class
})
public class PropsConfig {
}
