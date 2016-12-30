/**
 * 
 */
package com.sam.jcc.cloud.rules.service;

import com.sam.jcc.cloud.crud.ICRUD;

/**
 * @author Alec Kotovich
 * 
 */
public interface IService<T> extends ICRUD<T> {

	void findAndDelete(T entry);

}
