Feature: prova feature ritardatore

  @ritardatore
  Scenario: Prova comunicazione lambda
    Then invoco la lambda "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda"