/**
 * 
 */
package com.sam.jcc.cloud.rules.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author olegk
 *
 */
public class Rule {
	
	private final List<Rule> rules = new ArrayList<>();

	private Operation operation;

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public List<Rule> getRules() {
		return rules;
	}

	

}
