/**
 * 
 */
package com.sam.jcc.cloud.dataprovider.impl;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

import java.net.InetAddress;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
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
public class MongoDataProvider extends NoSqlDataProvider<AppData>implements IHealth {

	@Autowired
	private MongoDbInjector injector;

	@Autowired
	private MongoDatabaseManager dbManager;

	@Autowired
	private MongoSourceGenerator sourceGenerator;

	private static final long MONGO_PROVIDER_ID = 7L;

	public MongoDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public Long getId() {
		return MONGO_PROVIDER_ID;
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
	public IHealthMetadata checkHealth() {
		return new IHealthMetadata() {

			@Override
			public Long getId() {
				return MONGO_PROVIDER_ID;
			}

			@Override
			public String getName() {
				return getI18NDescription();
			}

			@Override
			public String getHost() {
				return getProperty("db.mongo.host");
			}

			@Override
			public String getPort() {
				return getProperty("db.mongo.port");
			}

			@Override
			public String getUrl() {
				try {
					String host = getHost();
					String hostName = host.equals("localhost") ? InetAddress.getLocalHost().getHostName() : host;
					MongoClient mongo = new MongoClient(host, Integer.valueOf(getPort()));
					MongoDatabase database = mongo.getDatabase("test");
					Document buildInfo = database.runCommand(new Document("buildInfo", 1));
					StringBuilder sb = new StringBuilder("mongo://");
					sb.append(hostName).append(":").append(getPort()).append("\n");
					sb.append("{ ");
					sb.append("version").append(" : ").append(buildInfo.get("version")).append(", ");
					sb.append("gitVersion").append(" : ").append(buildInfo.get("gitVersion")).append(", ");
					sb.append("targetMinOS").append(" : ").append(buildInfo.get("targetMinOS")).append(", ");
					sb.append("sysInfo").append(" : ").append(buildInfo.get("sysInfo")).append(", ");
					sb.append("debug").append(" : ").append(buildInfo.get("debug"));
					sb.append(" }");
					mongo.close();
					return sb.toString();
				} catch (Exception e) {
					try {
						String host = getHost();
						String hostName = host.equals("localhost") ? InetAddress.getLocalHost().getHostName() : host;
						StringBuilder sb = new StringBuilder();
						sb.append("mongo://").append(hostName).append(":").append(getPort()).append("\n");
						return sb.toString();
					} catch (Exception e2) {
						return null;
					}

				}
			}

			@Override
			public boolean isAlive() {
				try {
					String host = getHost();
					MongoClient mongo = new MongoClient(host, Integer.valueOf(getPort()));
					MongoDatabase database = mongo.getDatabase("test");
					database.runCommand(new Document("buildInfo", 1));
					mongo.close();
					return true;
				} catch (Exception e) {
					return false;
				}
			}

		};
	}

}
