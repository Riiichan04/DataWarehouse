package dao;

import models.CrawlResult;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface StagingDAO {
    //Truncate old data
    @SqlUpdate("""
        truncate table lottery_staging
    """)
    void truncateStaging();

    //Insert new data into staging
    @SqlUpdate("""
        insert into lottery_staging(date, prizeName, companyName, result, createdAt)
        values(:date, :prizeName, :companyName, :result, :createdAt)
    """)
    @RegisterBeanMapper(CrawlResult.class)
    int loadDataToStaging(@BindBean CrawlResult result);

    //Does input date exist in staging database
    @SqlQuery("""
        select 1
        from lottery_staging
        where date = :date
        limit 1
    """)
    boolean isDateExist(@Bind("date") String date);
}
