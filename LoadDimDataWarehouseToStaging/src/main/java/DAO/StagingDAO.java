package DAO;

import models.DimDate;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface StagingDAO {
    @SqlCall("""
            CALL insert_all_dim_company(:companyJson, :regionJson, :prizeJson)
            """)
    void loadDimStaging(String companyJson, String regionJson, String prizeJson);

    @SqlUpdate("""
        TRUNCATE TABLE dim_date;
    """)
    void truncateTableDate();

    @SqlBatch("""
        INSERT INTO dim_date(date, numberOfWeek)
        VALUES (:date, :numberOfWeek)
    """)
    void insertDimDateBatch(@BindBean List<DimDate> dateList);
}
