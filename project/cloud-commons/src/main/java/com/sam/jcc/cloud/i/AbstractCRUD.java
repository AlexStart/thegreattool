/**
 * 
 */
package com.sam.jcc.cloud.i;

import java.util.List;

/**
 * @author Alec Kotovich
 * 
 */
public abstract class AbstractCRUD<T> implements ICRUD<T> {

	@Override
	public abstract T create(T t);

	@Override
	public abstract T read(T t);

	@Override
	public abstract T update(T t);

	@Override
	public abstract void delete(T t);

	@Override
	public abstract List<? super T> findAll();

}
