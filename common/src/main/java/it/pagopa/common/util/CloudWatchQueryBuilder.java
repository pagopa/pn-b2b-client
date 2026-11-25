package it.pagopa.common.util;

import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;

import java.util.List;
import java.util.stream.Collectors;

public class CloudWatchQueryBuilder {

    public static FilterLogEventsRequest search(String microservice, String query, long minutes) {
        long startTime = System.currentTimeMillis() - (minutes * 60L * 1000L);
        return FilterLogEventsRequest.builder()
                .logGroupName(microservice)
                .filterPattern(query)
                .startTime(startTime)
                .build();
    }

    /**
     * Costruisce un filter pattern CloudWatch (AND implicito tra i termini) a partire da una lista di termini
     * testuali, ciascuno racchiuso tra virgolette per essere cercato come frase esatta. Non adatto a termini che
     * contengono il carattere {@code "}: la sintassi dei filter pattern di CloudWatch non consente di cercare
     * virgolette letterali (ne' con ne' senza escape), quindi un confronto che dipenda da quel carattere (es.
     * ancorare un campo di un log JSON con {@code "campo":"valore"}) va fatto lato client sul contenuto del log
     * gia' recuperato, non componendo un filter pattern piu' articolato.
     */
    public static String buildFilterPattern(List<String> terms) {
        return terms.stream().map(term -> "\"" + term + "\"").collect(Collectors.joining(" "));
    }
}
