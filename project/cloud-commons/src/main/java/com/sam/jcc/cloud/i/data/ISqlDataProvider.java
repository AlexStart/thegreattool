/**
 * 
 */
package com.sam.jcc.cloud.i.data;

import javax.sql.DataSource;

/**
 * @author olegk
 *
 */
public interface ISqlDataProvider extends IDataProvider {

	DataSource getDataSource();
}
