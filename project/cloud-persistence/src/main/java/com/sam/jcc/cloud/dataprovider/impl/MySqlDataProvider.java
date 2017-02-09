/**
 * 
 */
package com.sam.jcc.cloud.dataprovider.impl;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

import java.net.InetAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.data.IDBManager;
import com.sam.jcc.cloud.i.data.IDataInjector;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.ISourceGenerator;

/**
 * @author olegk
 *
 */
@Component
public class MySqlDataProvider extends SqlDataProvider<AppData>implements IHealth {

	private static final long MYSQL_PROVIDER_ID = 6L;

	@Autowired
	private MySqlInjector injector;

	@Autowired
	private MySqlDatabaseManager dbManager;

	@Autowired
	private JpaSourceGenerator sourceGenerator;

	public MySqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public Long getId() {
		return MYSQL_PROVIDER_ID;
	}

	@Override
	public IHealthMetadata checkHealth() {
		return new IHealthMetadata() {

			@Override
			public Long getId() {
				return MYSQL_PROVIDER_ID;
			}

			@Override
			public String getName() {
				return getI18NDescription();
			}

			@Override
			public String getHost() {
				try {
					URL url = new URL(getProperty("db.mysql.url").replaceAll("jdbc:mysql", "http")); // TODO
					// unknown
					// protocol:
					// jdbc
					return String.valueOf(url.getHost());
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public String getPort() {
				try {
					URL url = new URL(getProperty("db.mysql.url").replaceAll("jdbc:mysql", "http")); // TODO
					// unknown
					// protocol:
					// jdbc
					return String.valueOf(url.getPort());
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public String getUrl() {
				try {
					String host = getHost();
					Integer port = Integer.valueOf(getPort());
					String hostName = host.equals("localhost") ? InetAddress.getLocalHost().getHostName() : host;
					URL url = new URL("http", hostName, port, "/"); // TODO
																	// unknown
																	// protocol:
																	// jdbc
					DatabaseMetaDataDTO databaseConfiguration = getDatabaseConfiguration();

					StringBuilder sb = new StringBuilder();
					sb.append(url.toString().replaceAll("http", "jdbc:mysql")).append("\n");
					sb.append(databaseConfiguration.getDbName()).append(" ")
							.append(databaseConfiguration.getDbMajorVersion()).append(".")
							.append(databaseConfiguration.getDbMinorVersion()).append("\n");
					sb.append(databaseConfiguration.getDbProductVersion());
					return sb.toString();

				} catch (Exception e) {
					try {
						String host = getHost();
						String hostName = host.equals("localhost") ? InetAddress.getLocalHost().getHostName() : host;
						URL url = new URL("http", hostName, Integer.valueOf(getPort()), "/");
						return url.toString().replaceAll("http", "jdbc:mysql");
					} catch (Exception e2) {
						return null;
					}
				}
			}

			@Override
			public boolean isAlive() {
				try {
					Connection connection = getDataSource().getConnection();
					Validate.notNull(connection.getMetaData());
					connection.close();
					return true;
				} catch (Exception e) {
					return false;
				}
			}

			public DatabaseMetaDataDTO getDatabaseConfiguration() throws SQLException {
				Connection connection = getDataSource().getConnection();
				DatabaseMetaData metaData = connection.getMetaData();
				String dbName = metaData.getDatabaseProductName();
				String dbMajorVersion = String.valueOf(metaData.getDatabaseMajorVersion());
				String dbMinorVersion = String.valueOf(metaData.getDatabaseMinorVersion());
				String dbProductVersion = metaData.getDatabaseProductVersion();
				final DatabaseMetaDataDTO metaDataDTO = new DatabaseMetaDataDTO(dbName, dbMajorVersion, dbMinorVersion,
						dbProductVersion);
				connection.close();
				return metaDataDTO;
			}

			final class DatabaseMetaDataDTO {

				private final String dbName;
				private final String dbMajorVersion;
				private final String dbMinorVersion;
				private final String dbProductVersion;

				private DatabaseMetaDataDTO(String dbName, String dbMajorVersion, String dbMinorVersion,
						String dbProductVersion) {
					this.dbName = dbName;
					this.dbMajorVersion = dbMajorVersion;
					this.dbMinorVersion = dbMinorVersion;
					this.dbProductVersion = dbProductVersion;
				}

				public String getDbName() {
					return dbName;
				}

				public String getDbMajorVersion() {
					return dbMajorVersion;
				}

				public String getDbMinorVersion() {
					return dbMinorVersion;
				}

				public String getDbProductVersion() {
					return dbProductVersion;
				}

			}

		};
	}

	@Override
	public DataSource getDataSource() {
		return dbManager.getJdbcTemplate().getDataSource();
	}

	@Override
	protected ISourceGenerator<AppData> getSourceGenerator() {
		return sourceGenerator;
	}

	@Override
	protected IDataInjector<AppData> getDataInjector() {
		return injector;
	}

	@Override
	protected IDBManager<AppData> getDbManager() {
		return dbManager;
	}

	@Override
	public String getType() {
		return "mysql";
	}
}
