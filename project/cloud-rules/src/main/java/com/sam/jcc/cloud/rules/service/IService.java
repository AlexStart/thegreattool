/**
 * 
 */
package com.sam.jcc.cloud.rules.service;

import java.util.Map;

import com.sam.jcc.cloud.crud.ICRUD;

/**
 * @author Alec Kotovich
 * 
 */
public interface IService<T> extends ICRUD<T> {

	void findAndDelete(Map<String, String> props);

	T create(Map<?, ?> props);

	void delete(Map<String, String> props);

	T update(Map<?, ?> props);
	
	Map<Long, String> getNames();

}
