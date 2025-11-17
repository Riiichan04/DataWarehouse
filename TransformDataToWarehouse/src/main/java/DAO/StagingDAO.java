package DAO;

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
            CALL select_company_sk(:name)
            """)
    int getCompanySK(@Bind("name") String name);

    @SqlCall("""
            CALL select_prize_sk(:name)
            """)
    int getPrizeSK(@Bind("name") String name);

    @SqlCall("""
            CALL select_date_sk(:date)
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

}
