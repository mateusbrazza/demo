package br.com.seuprojeto.consents.utils;


import br.com.seuprojeto.consents.dto.response.MetaExternalResponseV2;

public class MetaUtils {

    private static final int FIXED_TOTAL_PAGES = 1;

    private MetaUtils() {}

    public static MetaExternalResponseV2 buildMetaPageableV2(int totalPages, int totalRecords) {
        return new MetaExternalResponseV2(totalPages, totalRecords);
    }

    public static MetaExternalResponseV2 buildMetaDefaultPageableV2(int totalRecords) {
        return new MetaExternalResponseV2(totalRecords, FIXED_TOTAL_PAGES);
    }
}
