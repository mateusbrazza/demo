package br.com.seuprojeto.consents.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Optional;

public class DirectoryUtils {

    private static final Logger log = LoggerFactory.getLogger(DirectoryUtils.class);

    private DirectoryUtils() {}

    public static String getDirectoryClientSupplementalInfo(String type, List<String> clientSupplementalInfo) {
        log.info("c=DirectoryUtils, m=getDirectoryClientSupplementalInfo, type={}", type);
        Pattern pattern = Pattern.compile("CLIENT_INFO_PATTERN"); // Supondo que CLIENT_INFO_PATTERN esteja definido
        return clientSupplementalInfo.stream()
                .map(pattern::matcher)
                .filter(Matcher::matches)
                .map(matcher -> matcher.group(3))
                .findFirst()
                .orElse("");
    }
}
