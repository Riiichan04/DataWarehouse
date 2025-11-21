package DAO;

import models.DimCompany;
import models.DimPrize;
import models.DimRegion;
import models.LotteryStaging;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;

import java.util.List;

public interface StagingDAO {
    @SqlCall("""
            CALL select_lottery_staging()
            """)
    @RegisterBeanMapper(LotteryStaging.class)
    List<LotteryStaging> getLotteryStaging();

    @SqlCall("""
            CALL find_company_sk(:name)
            """)
    int getCompanySK(@Bind("name") String name);

    @SqlCall("""
            CALL find_prize_sk(:name)
            """)
    int getPrizeSK(@Bind("name") String name);

    @SqlCall("""
            CALL find_date_sk(:date)
            """)
    int getDateSK(@Bind("date") String date);

    @SqlCall("""
            CALL insert_dim_company(:companyName)
            """)
    int insertCompany(@Bind("companyName") String companyName);

    @SqlCall("""
            CALL insert_dim_prize(:companyPrize)
            """)
    int insertPrize(@Bind("companyPrize") String companyPrize);

    @SqlCall("""
            CALL get_dim_company()
            """)
    @RegisterBeanMapper(DimCompany.class)
    List<DimCompany> getDimCompany();

    @SqlCall("""
            CALL get_dim_region()
            """)
    @RegisterBeanMapper(DimRegion.class)
    List<DimRegion> getDimRegion();

    @SqlCall("""
            CALL get_dim_company()
            """)
    @RegisterBeanMapper(DimPrize.class)
    List<DimPrize> getDimPrize();
}
