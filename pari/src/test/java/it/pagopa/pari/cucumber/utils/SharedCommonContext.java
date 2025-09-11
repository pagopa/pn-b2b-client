package it.pagopa.pari.cucumber.utils;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pari.cucumber.domain.JWTUserData;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@Slf4j
@ScenarioScope
public class SharedCommonContext {
    private JWTUserData userData;
    private List<ProductDTO> lastProductsUploaded;
    private String category;

}
