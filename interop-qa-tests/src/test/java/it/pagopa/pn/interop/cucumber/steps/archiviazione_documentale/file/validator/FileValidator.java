package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.*;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy.NdjsonValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy.PdfValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileValidator {

    private final TokenResolver tokenResolver;
    private final IFileTokenSource requiredTokens;
    private final IFileTokenSource optionalTokens;

    private final List<IValidationStrategy> strategies = List.of(
            new NdjsonValidationStrategy(),
            new PdfValidationStrategy()
    );

    public ValidationResult validate(ProcessedFile file) {

        IFileTokenSource resolvedRequired = resolveTokens(requiredTokens);
        IFileTokenSource resolvedOptional = resolveTokens(optionalTokens);

        IValidationStrategy strategy = strategies.stream()
                .filter(s -> s.supports(file.contentType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No validation strategy for content type " + file.contentType()
                        ));

        return strategy.validate(file, resolvedRequired, resolvedOptional);
    }

    private IFileTokenSource resolveTokens(IFileTokenSource source) {

        if (source == null) {
            return new ListFileTokenSource(List.of());
        }

        if (source instanceof IKeyedFileTokenSource keyedSource) {
            return MapFileTokenSource.of(
                    keyedSource.entries()
                            .map(e -> KeyedFileTokenEntry.of(
                                    e.key(),
                                    resolveToken(e.fileToken())
                            ))
                            .flatMap(e -> Stream.of(e.key(), e.fileToken()))
                            .toArray()
            );

        }

        if (source instanceof IListedFileTokenSource listedSource) {
            return new ListFileTokenSource(
                    listedSource.tokens()
                            .map(this::resolveToken)
                            .toList()
            );
        }

        // fallback sicuro
        return new ListFileTokenSource(
                source.tokens()
                        .map(this::resolveToken)
                        .toList()
        );
    }


    private FileToken resolveToken(FileToken token) {

        if (token.isValueToken()) {
            return FileToken.ofValue(
                    tokenResolver.resolve(token.expectedValue())
            );
        }

        if(token.isValidatorToken()) return token;

        throw new RuntimeException("Invalid token type");
    }
}
