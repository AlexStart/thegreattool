/**
 * 
 */
package com.sam.jcc.cloud.rules.model;

/**
 * @author olegk
 *
 */
public class Operation {

	public Operation(String name, Class<?> clazz) {
		super();
		this.name = name;
		this.operation = clazz.getName();
	}

	private String name;

	private String operation;

	private boolean allowed = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
