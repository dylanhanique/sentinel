package fr.sentinel.apiservice;

import fr.sentinel.apiservice.config.TestsDbConfig;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DuplicateKeyException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestsDbConfig.class)
public class ApiServiceDbTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void migration_v1_is_applied() {
        String query = "SELECT success FROM flyway_schema_history WHERE version = '1'";
        Boolean success = jdbcTemplate.queryForObject(query, Boolean.class);
        assertThat(success).isTrue();
    }

    @Test
    void account_contains_migration_columns() {
        String query = """
            SELECT column_name, udt_name, is_nullable
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_schema = current_schema()
            AND TABLE_NAME = 'account'
        """;

        record ColumnMeta(String type, String nullable) {}

        Map<String, ColumnMeta> columns = jdbcTemplate.query(query, rs -> {
            Map<String, ColumnMeta> map = new HashMap<>();
            while(rs.next()) {
                map.put(
                    rs.getString("column_name"),
                    new ColumnMeta(rs.getString("udt_name"), rs.getString("is_nullable"))
                );
            }
            return map;
        });

        assertThat(columns.keySet())
            .containsExactlyInAnyOrder("id", "email", "password_hash", "created_at");

        assertThat(columns)
            .containsEntry("id", new ColumnMeta("uuid", "NO"))
            .containsEntry("email", new ColumnMeta("citext", "NO"))
            .containsEntry("password_hash", new ColumnMeta("varchar", "NO"))
            .containsEntry("created_at", new ColumnMeta("timestamptz", "NO"));
    }

    @Test
    void email_uniqueness_is_case_insensitive() {
        String query1 = "INSERT INTO account (email, password_hash) VALUES ('test@email.com', 'password')";
        String query2 = "INSERT INTO account (email, password_hash) VALUES ('Test@email.com', 'password')";

        int rowInserted = jdbcTemplate.update(query1);
        assertThat(rowInserted).isEqualTo(1);
        assertThatThrownBy(() -> jdbcTemplate.update(query2)).isInstanceOf(DuplicateKeyException.class).hasMessageContaining("uk_account_email");
    }
}

