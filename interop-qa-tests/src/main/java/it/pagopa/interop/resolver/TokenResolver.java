package it.pagopa.interop.resolver;

import it.pagopa.interop.domain.Ente;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TokenResolver {

    public List<Ente> readProperty() {
        InputStream inputStream = null;
        List<Ente> enteList = new ArrayList<>();
        try {
            inputStream = new FileInputStream(new File("config/token-2.yaml"));
            Yaml yaml = new Yaml(new Constructor(Ente.class));
            yaml.loadAll(inputStream).forEach(i -> enteList.add((Ente) i));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return enteList;
    }
}
