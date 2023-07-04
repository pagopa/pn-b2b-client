package it.pagopa.pdnd;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class CreateClientAssertionApp {
    public static void main(String... args) throws NoSuchAlgorithmException, IOException, JOSEException, InvalidKeySpecException {
        // Parsing command-line arguments
        Options options = getOptions();

        // Parsing command line arguments
        CommandLine cmd = parsingOptions(options, args);

        // Reading private key from file
        String keyPath = cmd.getOptionValue("keyPath");
        String key = new String(Files.readAllBytes(Paths.get(keyPath)), Charset.defaultCharset());
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
//                .replaceAll(System.lineSeparator(), "")
                .replaceAll("\r\n?|\n", "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        // Creating RSA private key object
        RSAPrivateKey rsaPrivateKey = getPrivateKey(encoded);

        // Creating JWS header
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(cmd.getOptionValue("alg")))
                .keyID(cmd.getOptionValue("kid"))
                .type(new JOSEObjectType(cmd.getOptionValue("typ")))
                .build();

        // Creating JWT claims
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(cmd.getOptionValue("issuer"))
                .subject(cmd.getOptionValue("subject"))
                .audience(cmd.getOptionValue("audience"))
                .claim("purposeId", cmd.getOptionValue("purposeId"))
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(Duration.ofMinutes(43200))))
                .build();

        // Signing the JWT
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(new RSASSASigner(rsaPrivateKey));

        // Serializing the JWT to a string
        String clientAssertion = signedJWT.serialize();

        System.out.println(clientAssertion);


    }

    private static Options getOptions() {
        Options options = new Options();
        options.addRequiredOption("kid", "kid", true, "Key ID");
        options.addRequiredOption("alg", "alg", true, "Algorithm");
        options.addRequiredOption("typ", "typ", true, "Type");
        options.addRequiredOption("issuer", "issuer", true, "Issuer");
        options.addRequiredOption("subject", "subject", true, "Subject");
        options.addRequiredOption("audience", "audience", true, "Audience");
        options.addRequiredOption("purposeId", "purposeId", true, "Purpose ID");
        options.addRequiredOption("keyPath", "keyPath", true, "Key Path");

        return options;
    }

    private static CommandLine parsingOptions(Options options, String... args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar create-client-assertion.jar", options);
            System.exit(1);
        }
        return cmd;
    }

    private static RSAPrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var keyFactory = KeyFactory.getInstance("RSA");
        var keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}