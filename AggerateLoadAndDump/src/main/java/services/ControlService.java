package services;

import config.ControlConnector;
import dao.ControlDAO;

import java.sql.Timestamp;

public class ControlService {
    private ControlDAO controlDAO;

    //Config control later
    public ControlService() {
        this.controlDAO = ControlConnector.getInstance().onDemand(ControlDAO.class);
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