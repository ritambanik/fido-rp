package poc.samsung.fido.rp.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"poc.samsung.fido.rp.domain"})
@EnableJpaRepositories(basePackages = {"poc.samsung.fido.rp.repositories"})
@EnableTransactionManagement
public class RepositoryConfiguration {

}
