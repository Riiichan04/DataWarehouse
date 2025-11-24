import config.DatabaseConnection;
import config.LoadConfigOperator;
import enums.LogLevel;
import models.ProcessDetail;
import services.ControlService;
import services.LoadDataToStagingService;
import utils.DirectoryUtil;
import utils.OffsetLocalDate;

import java.io.File;
import java.sql.Timestamp;

public class LoadDataToStagingOperator {
    public static void main(String[] args) {
        //Get offset from param, default is 0 0 0 0
        OffsetLocalDate offset = new OffsetLocalDate();
        try {
            offset = new OffsetLocalDate(
                    Long.parseLong(args[0]),
                    Long.parseLong(args[1]),
                    Long.parseLong(args[2]),
                    Long.parseLong(args[3])
            );
        } catch (Exception e) {
            System.out.println("Invalid date offset, used zero offset instead.");
        }
        //Service
        LoadDataToStagingService service = new LoadDataToStagingService();
        ControlService control = new ControlService();
        //Get targetPath
        String targetPath = ProcessDetail.getInstance().getTargetPath();
        File[] listFile = DirectoryUtil.getAllFileByDate(targetPath, offset);

        if (listFile == null) return;
        //Get all file and load to staging
        for (File file : listFile) {
            try {
                service.transformAndLoadDataToStaging(file);
                control.addNewLog(
                        ProcessDetail.getInstance().getProcessId(),
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()),
                        LogLevel.SUCCESS.getLevel(),
                        "Load data from " + file.getName() + " to staging database success."
                );
            }
            catch (Exception e) {
                //Log here
                control.addNewLog(
                        ProcessDetail.getInstance().getProcessId(),
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()),
                        LogLevel.ERROR.getLevel(),
                        "Error when load data from " + file.getName() + " to staging database. Error detail: " + e.getMessage()
                );
            }
        }
    }
}
