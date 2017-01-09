package com.sam.jcc.cloud.i.app;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
public interface IAppMetadata {

	Long getId();
	
    String getProjectName();

    void setProjectName(String projectName);
}
