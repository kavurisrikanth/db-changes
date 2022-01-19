package security;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.internal.info.MigrationInfoImpl;
import org.flywaydb.core.internal.resolver.sql.SqlMigrationExecutor;
import org.flywaydb.core.internal.sqlscript.SqlScript;
import org.springframework.stereotype.Component;

import d3e.core.D3ELogger;

@Component
public class FlywayCallback implements Callback {

	boolean newDatabase;

	@PostConstruct
	public void init() {
		Flyway.configure().callbacks(this);

	}

	@Override
	public boolean supports(Event event, Context context) {
		return true;
	}

	@Override
	public boolean canHandleInTransaction(Event event, Context context) {
		return true;
	}

	@Override
	public void handle(Event event, Context context) {
		if (event == Event.BEFORE_MIGRATE) {
			try {
				ResultSet query = context.getConnection().createStatement()
						.executeQuery("select count(*) from flyway_schema_history");
				if (query.next()) {
					newDatabase = query.getInt(1) == 0;
				}
			} catch (SQLException e) {
				newDatabase = true;
			}
			return;
		}
		if (event == Event.BEFORE_EACH_MIGRATE) {
			MigrationInfo mig = context.getMigrationInfo();
			if (mig == null) {
				return;
			}
			MigrationInfoImpl info = (MigrationInfoImpl) mig;
			if (info.getVersion().getVersion().equals("0")) {
				return;
			}
			if (newDatabase) {
				SqlMigrationExecutor executor = (SqlMigrationExecutor) info.getResolvedMigration().getExecutor();
				try {
					Field script = executor.getClass().getDeclaredField("sqlScript");
					script.setAccessible(true);
					SqlScript obj = (SqlScript) script.get(executor);
					Field statements = obj.getClass().getDeclaredField("sqlStatements");
					statements.setAccessible(true);
					List stmts = (List) statements.get(obj);
					stmts.clear();
					D3ELogger.info("Skip migration for new database: " + info.getScript());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
