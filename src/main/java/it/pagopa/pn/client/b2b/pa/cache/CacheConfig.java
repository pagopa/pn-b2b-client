package it.pagopa.pn.client.b2b.pa.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configurazione Spring per i CacheManager singleton
 * Questi bean vengono creati una volta per scenario e riutilizzati
 */
@Configuration
@Slf4j
public class CacheConfig {

    /**
     * Cache singleton per sender tax ID (String -> String)
     * Chiave: "Comune_1" o "Comune_Multi"
     * Valore: il tax ID da DynamoDB
     * TTL: infinito fino a termine build
     */
    @Bean(name = "senderTaxIdCacheManager")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CacheManager<String, String> senderTaxIdCacheManager() {
        log.info("Creating Sender Tax ID Cache Manager (Singleton) with INFINITE TTL");
        return new CacheManager<>("SENDER_TAX_ID_CACHE", CacheManager.INFINITE_TTL);
    }
}

