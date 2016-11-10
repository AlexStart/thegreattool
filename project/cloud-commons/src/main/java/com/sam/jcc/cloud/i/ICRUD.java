/**
 * 
 */
package com.sam.jcc.cloud.i;

import java.util.List;

/**
 * @author Alec Kotovich
 * 
 */
public interface ICRUD<T> {

	T create(T t);

	T read(T t);

	T update(T t);

	void delete(T t);

	List<T> findAll();
}
