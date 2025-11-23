import services.TransformToDataWarehouse;

public class TransformDataToWarehouseOperator {
    public static void main(String[] args) {
        TransformToDataWarehouse transform = new TransformToDataWarehouse();
        transform.transform();
    }
}
