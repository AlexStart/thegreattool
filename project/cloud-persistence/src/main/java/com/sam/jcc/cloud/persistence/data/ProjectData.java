package com.sam.jcc.cloud.persistence.data;

import com.sam.jcc.cloud.i.Experimental;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Data
@Entity
@ToString(exclude = "sources")
@Experimental("Entity of all project data")
public class ProjectData {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String ci;
    private String vcs;
    private Boolean dataSupport = false;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] sources;
}
