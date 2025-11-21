package services;

import config.JDBIConnector;
import dao.ControlDAO;
import org.jdbi.v3.sqlobject.customizer.Bind;

import java.sql.Timestamp;

public class ControlService {
    private ControlDAO controlDAO;

    //Config control later
    public ControlService() {
        this.controlDAO = JDBIConnector.getInstance().onDemand(ControlDAO.class);
    }

    public boolean addNewLog(
            int processId,
            Timestamp startTime,
            Timestamp endTime,
            int status,
            String message
    ) {
        return controlDAO.insertLogProcess(processId, startTime, endTime, status, message) >= 1;
    }
}