/**
 *
 */
package com.sam.jcc.cloud.i.cq;

/**
 * @author Alec Kotovich
 */
public interface ICQMetadata {

    String getCQName();

    void setCQName(String name);

	String getProviderName();

	String getProviderDescription();
}
