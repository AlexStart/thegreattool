package com.sam.jcc.cloud.persistence.app;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Data
@Entity
public class AppMetadataEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String projectName;
}
