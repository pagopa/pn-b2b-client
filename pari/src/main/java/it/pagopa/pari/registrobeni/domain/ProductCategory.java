package it.pagopa.pari.registrobeni.domain;

import lombok.Getter;

import java.util.Arrays;

public enum ProductCategory {
    LAVATRICE("Lavatrice", "WASHINGMACHINES"),
    LAVASCIUGA("Lavasciuga", "WASHERDRIERS"),
    PIANO_COTTURA("Piano cottura", "RANGEHOODS"),
    APPARECCHIO_DI_REFRIGERAZIONE("Apperecchio di refrigerazione", "REFRIGERATINGAPPL"),
    ASCIUGATRICE("Asciugatrice", "TUMBLEDRYERS"),
    LAVASTOVIGLIE("Lavastoviglie", "DISHWASHERS"),
    CAPPA_DA_CUCINA("Cappa da cucina", "COOKINGHOBS"),
    FORNO("Forno", "OVENS");

    @Getter
    private final String description;
    private final String engCategory;

    ProductCategory(String description, String engCategory) {
        this.description = description;
        this.engCategory = engCategory;
    }

    public static String getEnglishCategory(String category) {
        return Arrays.stream(ProductCategory.values())
                .filter(c -> c.description.equalsIgnoreCase(category))
                .map(c -> c.engCategory)
                .findFirst()
                .orElseThrow();
    }


}


