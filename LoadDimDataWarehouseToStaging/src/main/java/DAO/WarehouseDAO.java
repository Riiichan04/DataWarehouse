package DAO;

import models.DimDate;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface WarehouseDAO {
    @SqlQuery("""
            CALL get_dim_company_json()
            """)
    String getDimCompanyJSON();

    @SqlQuery("""
            CALL get_dim_region_json()
            """)
    String getDimRegionJSON();

    @SqlQuery("""
            CALL get_dim_prize_json()
            """)
    String getDimPrizeJSON();

    @SqlQuery("""
            CALL get_dim_date()
            """)
    @RegisterBeanMapper(DimDate.class)
    List<DimDate> getDimDateList();
}
