package DAO;

import models.DimCompany;
import models.DimPrize;
import models.DimRegion;
import models.LotteryStaging;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface StagingDAO {
    @SqlQuery("""
            CALL select_lottery_staging()
            """)
    @RegisterBeanMapper(LotteryStaging.class)
    List<LotteryStaging> getLotteryStaging();

    @SqlQuery("""
            CALL find_company_sk(:name)
            """)
    int getCompanySK(@Bind("name") String name);

    @SqlQuery("""
            CALL find_prize_sk(:name)
            """)
    int getPrizeSK(@Bind("name") String name);

    @SqlQuery("""
            CALL find_date_sk(:date)
            """)
    int getDateSK(@Bind("date") String date);

    @SqlQuery("""
            CALL insert_dim_company(:companyName, :regionName)
            """)
    int insertCompany(@Bind("companyName") String companyName, @Bind("regionName") String regionName);

    @SqlQuery("""
            CALL insert_dim_prize(:companyPrize)
            """)
    int insertPrize(@Bind("companyPrize") String companyPrize);

    @SqlQuery("""
            SELECT * from dim_company
            """)
    @RegisterBeanMapper(DimCompany.class)
    List<DimCompany> getDimCompany();

    @SqlQuery("""
            SELECT * from dim_region
            """)
    @RegisterBeanMapper(DimRegion.class)
    List<DimRegion> getDimRegion();

    @SqlQuery("""
            SELECT * from dim_prize
            """)
    @RegisterBeanMapper(DimPrize.class)
    List<DimPrize> getDimPrize();
}
