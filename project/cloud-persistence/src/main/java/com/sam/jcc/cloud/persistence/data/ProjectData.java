package com.sam.jcc.cloud.persistence.data;

import com.sam.jcc.cloud.i.Experimental;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Blob;

import static com.sam.jcc.cloud.persistence.data.BlobUtil.*;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Data
@Entity
@Experimental("Entity of all project data")
public class ProjectData {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String ci;
    private String vcs;
    private Boolean dataSupport = false;

    @Access(AccessType.FIELD)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Blob data = EMPTY;

    public void setSources(byte[] sources) {
        data = blob(sources);
    }

    public byte[] getSources() {
        return bytes(data);
    }
}
