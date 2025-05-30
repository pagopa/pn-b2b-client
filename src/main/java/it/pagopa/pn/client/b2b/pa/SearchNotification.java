package it.pagopa.pn.client.b2b.pa;

import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;


@Slf4j
@SpringBootApplication
public class SearchNotification implements CommandLineRunner {
    private final MainBean mainBean;

    public SearchNotification(MainBean mainBean) {
        this.mainBean = mainBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(SearchNotification.class, args);
    }

    @Override
    public void run(String... args) {
        mainBean.doAll();
        System.exit(0);
    }

    @Component
    public static class MainBean {

        private final IPnPaB2bClient b2bClient;

        public MainBean(IPnPaB2bClient b2bClient) {
            this.b2bClient = b2bClient;
        }

        public void doAll() {
            log.info("MainBean Notification: {}", b2bClient.getSentNotificationV26("TPZH-WLML-JUXK-202206-P-1"));
        }
    }
}