package br.com.seuprojeto.consents.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.seuprojeto.consents.exceptions.validation.InvalidJsonConversionException;

public class CertificateExtractionUtils {

    private static final String EMPTY_STRING = "";
    private static final String SERIAL_NUMBER_PATTERN = "SERIALNUMBER=([^,]+)";
    private static final String CERTIFICATE_BEGIN_LINE = "-----BEGIN%20CERTIFICATE-----";
    private static final String CERTIFICATE_END_LINE = "-----END%20CERTIFICATE-----";
    private static final String CERTIFICATE_BREAK_LINE = "\\s+";
    private static final String CERTIFICATE_INSTANCE_TYPE = "X.509";

    private CertificateExtractionUtils() {}

    public static String extractParentOrgId(String certificate) {
        try {
            return Optional.ofNullable(certificate)
                    .map(CertificateExtractionUtils::removeUnusedChars)
                    .map(CertificateExtractionUtils::decode)
                    .map(CertificateExtractionUtils::buildCertificate)
                    .map(CertificateExtractionUtils::extractSubjectDN)
                    .map(CertificateExtractionUtils::extractSerialNumber)
                    .orElse(EMPTY_STRING);
        } catch (RuntimeException ignored) {
            return EMPTY_STRING;
        }
    }

    private static String extractSerialNumber(String subjectDN) {
        Pattern pattern = Pattern.compile(SERIAL_NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(subjectDN.toUpperCase());
        return Optional.of(subjectDN)
                .filter(sub -> matcher.find())
                .map(sub -> matcher.group(1))
                .orElse(EMPTY_STRING);
    }

    private static String extractSubjectDN(Certificate certificate) {
        return ((X509Certificate) certificate).getSubjectX500Principal().toString();
    }

    private static Certificate buildCertificate(byte[] certificateBytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(certificateBytes)) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(CERTIFICATE_INSTANCE_TYPE);
            return certificateFactory.generateCertificate(byteArrayInputStream);
        } catch (Exception e) {
            throw new InvalidJsonConversionException("MAT corpo da requisição para JSON: " + e.getMessage());
        }
    }

    private static byte[] decode(String certificate) {
        String certificateUrlDecoded = java.net.URLDecoder.decode(certificate, StandardCharsets.UTF_8);
        return Base64.getDecoder().decode(certificateUrlDecoded.getBytes());
    }

    private static String removeUnusedChars(String certificate) {
        return certificate
                .replace(CERTIFICATE_BEGIN_LINE, EMPTY_STRING)
                .replace(CERTIFICATE_END_LINE, EMPTY_STRING)
                .replaceAll(CERTIFICATE_BREAK_LINE, EMPTY_STRING);
    }
}
