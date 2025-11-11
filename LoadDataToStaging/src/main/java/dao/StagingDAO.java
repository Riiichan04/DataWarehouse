package dao;

import models.CrawlResult;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface StagingDAO {
    @SqlUpdate("""
        insert into lottery_staging(date, prizeName, companyName, result, createdAt)
        values(:date, :prizeName, :companyName, :result, :createdAt)
    """)
    @RegisterBeanMapper(CrawlResult.class)
    int loadDataToStaging(@BindBean CrawlResult result);
}
