import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class LoadAggregateToMartOperator {

    // 1. Đọc file JSON aggregate
    private static JsonObject loadAggregateFile(String filePath) throws Exception {
        return JsonParser.parseReader(new FileReader(filePath))
                .getAsJsonObject();
    }

    // 2. Kết nối database
    private static Connection connectDB(JsonObject dbConfig) throws Exception {
        String url = "jdbc:mysql://" +
                dbConfig.get("host").getAsString() + "/" +
                dbConfig.get("database").getAsString() +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        return DriverManager.getConnection(
                url,
                dbConfig.get("user").getAsString(),
                dbConfig.get("password").getAsString()
        );
    }

    // 3. Insert 1 bảng bất kỳ từ JSON vào DB
    private static void loadTable(Connection conn, String tableName, JsonArray rows) throws Exception {

        if (rows.size() == 0) {
            System.out.println("Bỏ qua bảng " + tableName + " (không có dữ liệu)");
            return;
        }

        System.out.println("Loading table: " + tableName + " — " + rows.size() + " rows");

        JsonObject first = rows.get(0).getAsJsonObject();
        List<String> columns = new ArrayList<>(first.keySet());

        String colString = String.join(",", columns);
        String placeholders = "?,".repeat(columns.size());
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        String sql = "INSERT INTO " + tableName + " (" + colString + ") VALUES (" + placeholders + ")";
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (JsonElement elem : rows) {
            JsonObject row = elem.getAsJsonObject();

            for (int i = 0; i < columns.size(); i++) {
                stmt.setObject(i + 1, row.get(columns.get(i)).getAsString());
            }

            stmt.execute();
        }

        System.out.println("Done table: " + tableName);
    }

    // 4. MAIN FLOW
    public static void main(String[] args) {
        System.out.println("BẮT ĐẦU LOAD AGGREGATE → MART");

        try {
            JsonObject config = JsonParser.parseReader(new FileReader("config.json"))
                    .getAsJsonObject();
            JsonObject martConfig = config.getAsJsonObject("mart");

            Connection martConn = connectDB(martConfig);

            JsonObject aggregateJson = loadAggregateFile("aggregate_24_11_2025.json");

            for (String key : aggregateJson.keySet()) {
                JsonArray rows = aggregateJson.getAsJsonArray(key);
                loadTable(martConn, key, rows);
            }

            System.out.println("HOÀN THÀNH LOAD AGGREGATE → MART");

        } catch (Exception e) {
            System.err.println("ERROR OCCURRED:");
            e.printStackTrace();
        }
    }
}
