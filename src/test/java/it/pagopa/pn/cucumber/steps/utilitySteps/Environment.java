package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;

@Getter
public enum Environment {

    DEV("dev"),
    TEST("test"),
    UAT("uat"),
    HOTFIX("hotfix");

    private String value;

    Environment(String value) {
        this.value = value;
    }
}
