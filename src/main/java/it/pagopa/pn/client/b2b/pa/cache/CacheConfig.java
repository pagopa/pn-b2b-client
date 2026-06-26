package it.pagopa.pn.client.b2b.pa.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configurazione Spring per i CacheManager singleton.
 * Questi bean vengono creati una sola volta per ApplicationContext e riutilizzati.
 */
@Configuration
@Slf4j
public class CacheConfig {

    /**
     * Cache Manager per i Codici Fiscali del mittente
     * TTL: INFINITE (i dati non cambiano e possono essere riutilizzati per più scenari)
     */
    @Bean(name = "senderTaxIdCacheManager")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CacheManager<String, String> senderTaxIdCacheManager() {
        log.info("Creating Sender Tax ID Cache Manager (Singleton) with INFINITE TTL");
        return new CacheManager<>("SENDER_TAX_ID_CACHE", CacheManager.INFINITE_TTL);
    }
}

