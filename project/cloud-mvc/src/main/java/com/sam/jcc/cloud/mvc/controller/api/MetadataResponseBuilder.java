package com.sam.jcc.cloud.mvc.controller.api;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Data
@Component
public class MetadataResponseBuilder {

    @Autowired
    private MetadataResolver metadataResolver;

    public <T> MetadataResponse<T> build(T data) {
        final MetadataResponse<T> response = new MetadataResponse<>();

        response.setData(data);
        response.setMetadata(metadataResolver.resolve(data));
        return response;
    }

    @Data
    public static class MetadataResponse<T> {
        private T data;
        private Map<String, Object> metadata;
    }
}
