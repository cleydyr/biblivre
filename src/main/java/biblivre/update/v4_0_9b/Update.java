package biblivre.update.v4_0_9b;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) throws SQLException {
		_fixUpdateTranslationFunction(connection);

		_fixUpdateUserFunction(connection);

		if (_checkBackupTableExistence(connection)) {
			_fixBackupTable(connection);
		}
	}

	private void _fixBackupTable(Connection connection) throws SQLException {
		String createGlobalBackupTableSQL = "CREATE TABLE global.backups (id serial NOT NULL, " +
				"created timestamp without time zone NOT NULL DEFAULT now(), " +
				"path character varying, " +
				"schemas character varying NOT NULL, " +
				"type character varying NOT NULL, " +
				"scope character varying NOT NULL, " +
				"downloaded boolean NOT NULL DEFAULT false, " +
				"steps integer, " +
				"current_step integer, " +
				"CONSTRAINT \"PK_backups\" PRIMARY KEY (id) " +
				") WITH (OIDS=FALSE);";

		String alterGlobalBackupTableOwnership = "ALTER TABLE global.backups OWNER TO biblivre;";

		try (Statement statement = connection.createStatement()) {
			statement.execute(createGlobalBackupTableSQL);
			statement.execute(alterGlobalBackupTableOwnership);
		};
	}

	private boolean _checkBackupTableExistence(Connection connection) throws SQLException {
		String countBackupTablesSQL = "SELECT count(*) as count FROM information_schema.tables " +
				"WHERE table_schema = 'global' and table_name = 'backup';";

		try (Statement statement = connection.createStatement()) {

			ResultSet countResult = statement.executeQuery(countBackupTablesSQL);

			return countResult.next() && countResult.getInt("count") == 1;
		}
	}

	private static void _fixUpdateUserFunction(Connection connection) throws SQLException {
		String sql = "CREATE OR REPLACE FUNCTION update_user_value(integer, character varying, character varying, character varying) RETURNS integer \n" +
				"  LANGUAGE plpgsql \n" +
				"    AS $_$ \n" +
				" DECLARE \n" +
				"	p_id ALIAS FOR $1; \n" +
				"	p_key ALIAS FOR $2; \n" +
				"	p_value ALIAS FOR $3; \n" +
				"	p_ascii ALIAS FOR $4; \n" +
				" \n" +
				"	v_schema character varying; \n" +
				"	v_current_value TEXT; \n" +
				" BEGIN \n" +
				"	v_schema = current_schema(); \n" +
				" \n" +
				"	IF v_schema = 'global' THEN \n" +
				"		-- Can't save user fields in global schema \n" +
				"		RETURN 1; \n" +
				"	END IF; \n" +
				" \n" +
				"	-- Get the current value for this key \n" +
				"	EXECUTE 'SELECT value FROM ' || pg_catalog.quote_ident(v_schema) || '.users_values WHERE user_id = ' || pg_catalog.quote_literal(p_id) || ' AND key = ' || pg_catalog.quote_literal(p_key) INTO v_current_value; \n" +
				"	-- SELECT INTO v_current_value value FROM users_values WHERE user_id = p_id AND key = p_key; \n" +
				" \n" +
				"	-- If the new value is the same as the current one, \n" +
				"	-- return \n" +
				"	IF v_current_value = p_value THEN \n" +
				"		RETURN 2; \n" +
				"	END IF; \n" +
				" \n" +
				"	-- If the current value is null then there is no \n" +
				"	-- current value for this key, then we should \n" +
				"	-- insert it \n" +
				"	IF v_current_value IS NULL THEN \n" +
				"		-- RAISE LOG 'inserting into schema %', v_schema; \n" +
				"		EXECUTE 'INSERT INTO ' || pg_catalog.quote_ident(v_schema) || '.users_values (user_id, key, value, ascii) VALUES (' || pg_catalog.quote_literal(p_id) || ', ' || pg_catalog.quote_literal(p_key) || ', ' || pg_catalog.quote_literal(p_value) || ', ' || pg_catalog.quote_literal(p_ascii) || ');'; \n" +
				"		--INSERT INTO users_values (user_id, key, value, ascii) VALUES (p_id, p_key, p_value, p_ascii); \n" +
				"		 \n" +
				"		RETURN 3; \n" +
				"	ELSE \n" +
				"		EXECUTE 'UPDATE ' || pg_catalog.quote_ident(v_schema) || '.users_values SET value = ' || pg_catalog.quote_literal(p_value) || ', ascii = ' || pg_catalog.quote_literal(p_ascii) || ' WHERE user_id = ' || pg_catalog.quote_literal(p_id) || ' AND key = ' || pg_catalog.quote_literal(p_key); \n" +
				"		-- UPDATE users_values SET value = p_value, ascii = p_ascii WHERE user_id = p_id AND key = p_key; \n" +
				" \n" +
				"		RETURN 4; \n" +
				"	END IF; \n" +
				" END; \n" +
				"$_$; ";

		String sql2 = "ALTER FUNCTION global.update_user_value(integer, character varying, character varying, character varying) OWNER TO biblivre;";

		Statement st = connection.createStatement();
		st.execute(sql);
		st.execute(sql2);
	}

	private static void _fixUpdateTranslationFunction(Connection con) throws SQLException {
		String sql = "CREATE OR REPLACE FUNCTION update_translation(character varying, character varying, character varying, integer) RETURNS integer \n" +
				"    LANGUAGE plpgsql \n" +
				"    AS $_$ \n" +
				" DECLARE \n" +
				"	p_language ALIAS FOR $1; \n" +
				"	p_key ALIAS FOR $2; \n" +
				"	p_text ALIAS FOR $3; \n" +
				"	p_user ALIAS FOR $4; \n" +
				" \n" +
				"	v_schema character varying; \n" +
				"	v_current_value TEXT; \n" +
				"	v_global_value TEXT; \n" +
				"	v_user_created BOOLEAN; \n" +
				"	v_query_string character varying; \n" +
				" BEGIN \n" +
				"	v_schema = current_schema(); \n" +
				"	 \n" +
				"	IF v_schema <> 'global' THEN \n" +
				"		-- Get the global value for this key \n" +
				"		SELECT INTO v_global_value text FROM global.translations \n" +
				"		WHERE language = p_language AND key = p_key; \n" +
				" \n" +
				"		-- If the new text is the same as the global one, \n" +
				"		-- delete it from the current schema \n" +
				"		IF v_global_value = p_text THEN \n" +
				"			-- Fix for unqualified schema in functions          \n" +
				"			EXECUTE 'DELETE FROM ' || pg_catalog.quote_ident(v_schema) || '.translations WHERE language = ' || pg_catalog.quote_literal(p_language) || ' AND key = ' || pg_catalog.quote_literal(p_key); \n" +
				"			-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
				"			-- DELETE FROM translations WHERE language = p_language AND key = p_key; \n" +
				"			RETURN 1; \n" +
				"		END IF; \n" +
				"	END IF; \n" +
				" \n" +
				"	-- Get the current value for this key \n" +
				"	 \n" +
				"	-- Fix for unqualified schema in functions          \n" +
				"	EXECUTE 'SELECT text FROM ' || pg_catalog.quote_ident(v_schema) || '.translations WHERE language = ' || pg_catalog.quote_literal(p_language) || ' AND key = ' || pg_catalog.quote_literal(p_key) INTO v_current_value; \n" +
				"	-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
				"	-- SELECT INTO v_current_value text FROM translations WHERE language = p_language AND key = p_key; \n" +
				"	 \n" +
				"	-- If the new text is the same as the current one, \n" +
				"	-- return \n" +
				"	IF v_current_value = p_text THEN \n" +
				"		RETURN 2; \n" +
				"	END IF; \n" +
				" \n" +
				"	-- If the new key isn't available in the global schema, \n" +
				"	-- then this is a user_created key \n" +
				"	v_user_created = v_schema <> 'global' AND v_global_value IS NULL; \n" +
				" \n" +
				"	-- If the current value is null then there is no \n" +
				"	-- current translation for this key, then we should \n" +
				"	-- insert it \n" +
				"	IF v_current_value IS NULL THEN     \n" +
				"		EXECUTE 'INSERT INTO ' || pg_catalog.quote_ident(v_schema) || '.translations (language, key, text, created_by, modified_by, user_created) VALUES (' || pg_catalog.quote_literal(p_language) || ', ' || pg_catalog.quote_literal(p_key) || ', ' || pg_catalog.quote_literal(p_text) || ', ' || pg_catalog.quote_literal(p_user) || ', ' || pg_catalog.quote_literal(p_user) || ', ' || pg_catalog.quote_literal(v_user_created) || ');'; \n" +
				" \n" +
				"		-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
				"		--INSERT INTO translations \n" +
				"		--(language, key, text, created_by, modified_by, user_created) \n" +
				"		--VALUES \n" +
				"		--(p_language, p_key, p_text, p_user, p_user, v_user_created); \n" +
				"		 \n" +
				"		RETURN 3; \n" +
				"	ELSE \n" +
				"		EXECUTE 'UPDATE ' || pg_catalog.quote_ident(v_schema) || '.translations SET text = ' || pg_catalog.quote_literal(p_text) || ', modified = now(), modified_by = ' || pg_catalog.quote_literal(p_user) || ' WHERE language = ' || pg_catalog.quote_literal(p_language) || ' AND key = ' || pg_catalog.quote_literal(p_key); \n" +
				" \n" +
				"		-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
				"		--UPDATE translations \n" +
				"		--SET text = p_text, \n" +
				"		--modified = now(), \n" +
				"		--modified_by = p_user \n" +
				"		--WHERE language = p_language AND key = p_key; \n" +
				"		 \n" +
				"		RETURN 4; \n" +
				"	END IF; \n" +
				" END; \n" +
				" $_$; ";

		String sql2 = "ALTER FUNCTION global.update_translation(character varying, character varying, character varying, integer) OWNER TO biblivre;";

		Statement st = con.createStatement();
		st.execute(sql);
		st.execute(sql2);
	}

	@Override
	public String getVersion() {
		return "4.0.9b";
	}

}
