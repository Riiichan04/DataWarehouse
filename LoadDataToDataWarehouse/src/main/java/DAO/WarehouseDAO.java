package DAO;

import models.FactLottery;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlCall;

import java.util.List;

public interface WarehouseDAO {
    @SqlBatch("""
            INSERT INTO fact_prize_results(dateId, prizeId, companyId, result)
            values(:dateId, :prizeId, :companyId, :result)
            """)
    void insertWarehouse(@BindBean List<FactLottery> factLotteries);

    @SqlCall("""
            CALL load_dim_company_json(:dimCompanyJson)
            """)
    void loadDimCompanyJson(@Bind("dimCompanyJson") String dimCompanyJson);

    @SqlCall("""
            CALL load_dim_region_json(:dimRegionJson)
            """)
    void loadDimRegionJson(@Bind("dimRegionJson") String dimRegionJson);

    @SqlCall("""
            CALL load_dim_prize_json(:dimPrizeJson)
            """)
    void loadDimPrizeJson(@Bind("dimPrizeJson") String dimPrizeJson);
}
