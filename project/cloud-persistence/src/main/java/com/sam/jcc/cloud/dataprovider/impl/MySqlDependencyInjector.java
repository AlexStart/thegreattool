package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.project.DependencyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Component
class MySqlDependencyInjector {

    @Autowired
    private DependencyManager dependencyManager;

    @Autowired
    private ZipArchiveManager zipManager;

    public void inject(AppData data) {
    }
}
