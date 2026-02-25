package it.pagopa.pn.cucumber.steps.pa.strategies;

import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.SelectiveUpdateRegistryRequestV2;

import java.util.*;
import java.util.function.BiConsumer;

import static java.util.Map.entry;

public class SelectiveUpdateStrategies {

    private static final Map<String, BiConsumer<SelectiveUpdateRegistryRequestV2, String>> STRATEGIES =
        Map.ofEntries(
                entry("description", (req, val) -> req.setDescription(val)),
                entry("phoneNumbers", (req, val) -> req.setPhoneNumbers(parseList(val))),
                entry("externalCodes", (req, val) -> req.setExternalCodes(parseList(val))),
                entry("addressRow", (req, val) -> req.getAddress().setAddressRow(val)),
                entry("addressCap", (req, val) -> req.getAddress().setCap(val)),
                entry("addressCity", (req, val) -> req.getAddress().setCity(val)),
                entry("addressProvince", (req, val) -> req.getAddress().setProvince(val)),
                entry("addressCountry", (req, val) -> req.getAddress().setCountry(val)),
                entry("email", (req, val) -> req.setEmail(val)),
                entry("openingTime", (req, val) -> req.setOpeningTime(val)),
                entry("startValidity", (req, val) -> req.setStartValidity(val)),
                entry("endValidity", (req, val) -> req.setEndValidity(val)),
                entry("website", (req, val) -> req.setWebsite(val)),
                entry("appointmentRequired", (req, val) -> req.setAppointmentRequired(val == null ? null : Boolean.parseBoolean(val)))
        );

    public static void apply(SelectiveUpdateRegistryRequestV2 request, String field, String value) {
        BiConsumer<SelectiveUpdateRegistryRequestV2, String> strategy = STRATEGIES.get(field);
        if (strategy == null) throw new IllegalArgumentException("Campo non gestito nel PUT Selective: " + field);
        strategy.accept(request, value);
    }

    private static List<String> parseList(String value) {
        if (value == null) return null;
        if ("[]".equals(value)) return List.of();
        return Arrays.stream(value.split(",")).map(String::trim).toList();
    }
}