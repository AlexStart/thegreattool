package com.sam.jcc.cloud.persistence.project;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Data
@Entity
public class Dependency {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
