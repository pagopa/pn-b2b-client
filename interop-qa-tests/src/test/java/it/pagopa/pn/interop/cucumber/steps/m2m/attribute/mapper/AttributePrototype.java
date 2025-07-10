package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class AttributePrototype {

    public String name;
    public String description;
    public String code;
}
