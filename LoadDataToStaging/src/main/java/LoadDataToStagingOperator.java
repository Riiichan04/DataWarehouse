import config.DatabaseConnection;
import config.LoadConfigOperator;
import models.ProcessDetail;
import services.ControlService;
import services.LoadDataToStagingService;
import utils.DirectoryUtil;
import utils.OffsetLocalDate;

import java.io.File;

public class LoadDataToStagingOperator {
    public static void main(String[] args) {
        OffsetLocalDate offset = new OffsetLocalDate();
        try {
            offset = new OffsetLocalDate(
                    Long.parseLong(args[0]),
                    Long.parseLong(args[1]),
                    Long.parseLong(args[2]),
                    Long.parseLong(args[3])
            );
        } catch (NumberFormatException e) {
            System.out.println("Invalid date offset, used zero offset instead.");
        }
        LoadDataToStagingService service = new LoadDataToStagingService();
//        ControlService control = new ControlService();

        String targetPath = ProcessDetail.getInstance().getTargetPath();
        File[] listFile = DirectoryUtil.getAllFileByDate(targetPath, offset);

        if (listFile == null) return;
        for (File file : listFile) {
            try {
                //FIXME: Add config here
//                control.addNewProcess();
                service.transformAndLoadDataToStaging(file);
            }
            catch (Exception e) {
                //Log here
            }
        }
    }
}
