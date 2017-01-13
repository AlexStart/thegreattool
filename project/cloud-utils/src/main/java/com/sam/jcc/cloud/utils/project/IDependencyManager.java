package com.sam.jcc.cloud.utils.project;

import java.io.File;
import java.util.List;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
interface IDependencyManager<T> {

    String add(File config, T dependency);

    List<T> getAllDependencies(File config);
}
