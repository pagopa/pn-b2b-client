package it.pagopa.pn.cucumber.steps.informalNotification.InformalDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InformalMessageContent {

    private final String language;
    private final String subject;
    private final String longBody;
    private final String shortBody;
}

