package services;

import config.WarehouseConnector;
import dao.AggregateDAO;
import enums.LogLevel;
import models.ProcessDetail;

import java.sql.Timestamp;

public class AggregateService {
    private AggregateDAO aggerateDAO;
    private ControlService controlService;

    public AggregateService() {
        controlService = new ControlService();
//        aggerateDAO = WarehouseConnector.getInstance().onDemand(AggerateDAO.class);
    }

    public void statisticAggerateData() {
        //FIXME: Create a temp transaction, will be changed to query later
        //FIXME: Change this to a procedure
        try {
            WarehouseConnector.getInstance().useTransaction(handle -> {
                aggerateDAO = handle.attach(AggregateDAO.class);
                aggerateDAO.truncateAggHistoryPrizes();
                aggerateDAO.truncateAggResultFrequencies();
                aggerateDAO.truncateAggPairFrequencies();

                aggerateDAO.statisticMostAppearReward();
                aggerateDAO.statisticResultFrequencies();
                aggerateDAO.statisticPairFrequencies();
            });
        }
        catch (Exception e) {
            //Log error
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.ERROR.getLevel(),
                    "Insert aggregate data failed."
            );
        }
    }
}
