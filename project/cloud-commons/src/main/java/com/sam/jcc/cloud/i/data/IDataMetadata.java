/**
 *
 */
package com.sam.jcc.cloud.i.data;

/**
 * @author Alec Kotovich
 */
public interface IDataMetadata {

    String getDbName();

    void setDbName(String name);

	String getProviderName();

	String getProviderDescription();
}
