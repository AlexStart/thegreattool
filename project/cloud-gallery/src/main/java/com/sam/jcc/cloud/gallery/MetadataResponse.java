package com.sam.jcc.cloud.gallery;

import lombok.Data;

import java.util.Map;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Data
public class MetadataResponse<T> {

    private T data;

    private Map<String, Object> type;

    private static final MetadataResolver METADATA_RESOLVER = new MetadataResolver();

    public static <T> MetadataResponse<T> response(T data) {
        final MetadataResponse<T> response = new MetadataResponse<>();

        response.setData(data);
        response.setType(METADATA_RESOLVER.describe(data));
        return response;
    }
}
