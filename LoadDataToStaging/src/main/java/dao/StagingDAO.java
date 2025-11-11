package dao;

import models.CrawlResult;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface StagingDAO {
    @SqlUpdate("""
        insert into lottery_staging(date, prizeName, companyName, result, createdAt)
        values(:date, :prizeName, :companyName, :result, :createdAt)
    """)
    @RegisterBeanMapper(CrawlResult.class)
    int loadDataToStaging(@BindBean CrawlResult result);

    @SqlQuery("""
        select count(id)
        from lottery_staging
        where date = :date
    """)
    boolean isDateExist(@Bind("date") String date);
}
