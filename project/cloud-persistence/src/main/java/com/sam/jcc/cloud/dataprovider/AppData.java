package com.sam.jcc.cloud.dataprovider;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import lombok.Data;
import lombok.ToString;

import java.io.File;

import static com.sam.jcc.cloud.dataprovider.AppDataStatus.INITIALIZED;


/**
 * @author Alexey Zhytnik
 * @since 15-Jan-17
 */
@Data
@ToString(of = {"appName", "status"})
public class AppData implements IDataMetadata, IStatusable {

    private String appName;
    private AppDataStatus status = INITIALIZED;

    @Experimental("maybe remove or marks with @JsonIgnore")
    private transient byte[] sources;

    private File location;
}