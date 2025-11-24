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
        insert into lottery_staging(date, prizeName, companyName, result, createdAt, regionName)
        values(:date, :prizeName, :companyName, :result, now(), :regionName)
    """)
    void loadDataToStaging(
            @Bind("date") String date,
            @Bind("prizeName") String prizeName,
            @Bind("companyName") String companyName,
            @Bind("result") String result,
            @Bind("regionName") String regionName
    );

    //Does input date exist in staging database
    @SqlQuery("""
        select exists(
            select 1
            from lottery_staging
            where date = :date and companyName = :companyName
        )
    """)
    boolean isDateExist(@Bind("date") String date, @Bind("companyName") String regionName);
}
