import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import config.LoadConfigOperator;
import dao.ControlDAO;
import models.ProcessDetail;
import services.ControlService;
import services.DataMartService;

public class LoadAggregateToMartOperator {
    public static void main(String[] args) {
        DataMartService martService = new DataMartService();
        martService.loadFromJson(ProcessDetail.getInstance().getTargetPath());
    }
}
