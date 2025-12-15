package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IKeyedFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy.JsonValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy.NdjsonValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy.PdfValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class FileValidator {

    private final TokenResolver tokenResolver;
    private final IFileTokenSource requiredTokens;
    private final IFileTokenSource optionalTokens;

    private final List<IValidationStrategy> strategies = List.of(
            new JsonValidationStrategy(),
            new NdjsonValidationStrategy(),
            new PdfValidationStrategy()
    );

    public ValidationResult validate(ProcessedFile file) {
        return new ValidationResult(validateTokens(file, requiredTokens), validateTokens(file, optionalTokens));
    }

    private Set<String> validateTokens(ProcessedFile file, IFileTokenSource tokens) {

        if (tokens == null) {
            return Set.of();
        }

        IFileTokenSource resolved = resolveTokens(tokens);

        IValidationStrategy strategy = strategies.stream()
                .filter(s -> s.supports(file.contentType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No validation strategy for content type " + file.contentType()
                        ));

        boolean valid = strategy.validate(file, resolved);

        return valid ? Set.of() : extractKeys(resolved);
    }

    private Set<String> extractKeys(IFileTokenSource source) {

        if (source instanceof IKeyedFileTokenSource keyed) {
            return keyed.entries()
                    .map(KeyedFileTokenEntry::key)
                    .collect(Collectors.toSet());
        }

        return source.tokens()
                .filter(FileToken::isValueToken)
                .map(FileToken::expectedValue)
                .collect(Collectors.toSet());
    }

    private IFileTokenSource resolveTokens(IFileTokenSource source) {

        if (source instanceof IKeyedFileTokenSource keyedSource) {

            return (IKeyedFileTokenSource) () ->
                    keyedSource.entries()
                            .map(e -> KeyedFileTokenEntry.of(
                                    e.key(),
                                    resolveToken(e.fileToken())
                            ));
        }

        return () -> source.tokens().map(this::resolveToken);
    }

    private FileToken resolveToken(FileToken token) {

        if (token.isValueToken()) {
            return FileToken.ofValue(
                    tokenResolver.resolve(token.expectedValue())
            );
        }

        // validator token: NON va risolto
        return token;
    }
}
