import services.LoadDataToDataWarehouse;

public class LoadDataToDataWarehouseOperator {
    public static void main(String[] args) {
        LoadDataToDataWarehouse loadDataToDataWarehouse = new LoadDataToDataWarehouse();
        loadDataToDataWarehouse.load();
    }
}
