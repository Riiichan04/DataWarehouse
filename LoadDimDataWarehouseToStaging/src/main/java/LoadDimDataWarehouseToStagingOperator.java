import services.LoadDimDataWarehouseToStaging;

public class LoadDimDataWarehouseToStagingOperator {
    public static void main(String[] args) {
        LoadDimDataWarehouseToStaging loader = new LoadDimDataWarehouseToStaging();
        loader.loadDimDataWarehouseToStaging();
    }
}
