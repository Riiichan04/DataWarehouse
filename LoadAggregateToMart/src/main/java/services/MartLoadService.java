package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dao.MartDAOFactory;
import dao.MartDAO;

import java.util.ArrayList;
import java.util.List;

public class MartLoadService {

    private final MartDAO martDAO = MartDAOFactory.getInstance();

    public int loadTable(String tableName, JsonArray rows) {

        if (rows.size() == 0) {
            return 0;
        }

        // Lấy danh sách cột từ record đầu tiên
        JsonObject first = rows.get(0).getAsJsonObject();
        List<String> columns = new ArrayList<>(first.keySet());

        // Truncate table
        martDAO.truncate(tableName);

        // Insert từng row
        for (JsonElement element : rows) {
            JsonObject row = element.getAsJsonObject();
            List<Object> values = new ArrayList<>();

            for (String col : columns) {
                values.add(row.get(col).getAsString());
            }

            martDAO.insertRow(
                    tableName,
                    String.join(",", columns),
                    values.toArray()
            );
        }

        return rows.size();
    }
}

