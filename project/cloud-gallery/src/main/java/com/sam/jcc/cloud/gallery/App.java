package com.sam.jcc.cloud.gallery;

import lombok.Data;

import java.util.UUID;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Data
public class App {

    private UUID id;
    private String name;
    private Boolean disabled;
}
