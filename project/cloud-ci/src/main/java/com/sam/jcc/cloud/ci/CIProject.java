package com.sam.jcc.cloud.ci;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CONFIGURED;

import java.io.File;

import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.ci.ICIMetadata;

import lombok.Data;
import lombok.ToString;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
@Data
@ToString(of = { "artifactId", "status" })
public class CIProject implements ICIMetadata, IStatusable {

	public final static String CI_PREFIX = getProperty("ci.prefix");

	private File sources;
	private byte[] build;
	private String artifactId;
	private CIProjectStatus status = CONFIGURED;

	/**
	 * Generates a identifier of CIProject. Used only for internal module
	 * proposes.
	 */
	public String getName() {
		return CI_PREFIX + artifactId;
	}

	/**
	 * Recoveries a CIProject information. Used only for internal module
	 * proposes.
	 */
	public void setName(String name) {
		artifactId = name.substring(CI_PREFIX.length());
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
