package dao;

import org.jdbi.v3.core.Jdbi;

public class MartDAO {

    private final Jdbi jdbi;

    public MartDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void truncate(String tableName) {
        jdbi.useHandle(handle ->
                handle.execute("TRUNCATE TABLE " + tableName)
        );
    }

    public void insertRow(String tableName, String columns, Object[] values) {
        jdbi.useHandle(handle -> {

            // tạo placeholders: "?, ?, ?, ?"
            String placeholder = "?, ".repeat(values.length);
            placeholder = placeholder.substring(0, placeholder.length() - 2);

            String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholder + ")";

            var query = handle.createUpdate(sql);

            // bind từng phần tử
            for (int i = 0; i < values.length; i++) {
                query.bind(i, values[i]);
            }

            query.execute();
        });
    }

    public boolean exists(String tableName) {
        return jdbi.withHandle(handle ->
                handle.createQuery(
                        "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :table"
                )
                .bind("table", tableName)
                .mapTo(Integer.class)
                .one()
        ) > 0;
    }
}
