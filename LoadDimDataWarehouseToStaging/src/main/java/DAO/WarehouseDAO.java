package DAO;

import models.DimDate;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlCall;

import java.util.List;

public interface WarehouseDAO {
    @SqlCall("""
            CALL get_dim_company_json()
            """)
    String getDimCompanyJSON();

    @SqlCall("""
            CALL get_dim_region_json()
            """)
    String getDimRegionJSON();

    @SqlCall("""
            CALL get_dim_prize_json()
            """)
    String getDimPrizeJSON();

    @SqlCall("""
            CALL get_dim_date()
            """)
    @RegisterBeanMapper(DimDate.class)
    List<DimDate> getDimDateList();
}
