package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import lombok.Data;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Data
public class AppMetadata implements IAppMetadata, IStatusable {

    private Long id;
    private String projectName;
    private AppMetadataStatus status;
}
