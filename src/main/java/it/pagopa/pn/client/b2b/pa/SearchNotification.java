package it.pagopa.pn.client.b2b.pa;

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
        System.exit( 0 );
    }

    @Component
    public static class MainBean {
        private final PnPaB2bUtils utils;

        public MainBean(PnPaB2bUtils utils) {
            this.utils = utils;
        }

        public void doAll() {
            log.info("MainBean Notification: {}",utils.getNotificationByIun( "TPZH-WLML-JUXK-202206-P-1" ) );
        }
    }
}