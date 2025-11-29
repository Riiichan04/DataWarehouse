package dao;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface AggregateDAO {
    @SqlUpdate("""
        truncate table agg_prize_result_freq
    """)
    void truncateAggHistoryPrizes();

    @SqlUpdate("""
        truncate table agg_number_week_result_freq
    """)
    void truncateAggResultFrequencies();

    @SqlUpdate("""
        truncate table agg_tail_prize_freq
    """)
    void truncateAggPairFrequencies();

    @SqlUpdate("""
        truncate table agg_region_result_freq
    """)
    void truncateAggRegionResultFrequencies();

    @SqlUpdate("""
        insert into agg_prize_result_freq (
            prizeId, result, createdAt, frequency
        )
        select f.prizeId, f.result, now() as createdAt, count(f.prizeId) as frequency
        from fact_prize_results f
        group by f.prizeId, f.result
    """)
    void statisticMostAppearReward();

    @SqlUpdate("""
        insert into agg_number_week_result_freq (
            result, numberOfWeek, createdAt, prizeId, frequency
        )
        select f.result, d.numberOfWeek, now() as createdAt, f.prizeId, count(f.result) as frequency
        from fact_prize_results f join dim_date d on f.dateId = d.id
        group by f.prizeId, f.result, d.numberOfWeek
    """)
    void statisticResultFrequencies();

    @SqlUpdate("""
        insert into agg_tail_prize_freq (
            tailResult, prizeId, createdAt, frequency
        )
        select
                substring(f.result, length(f.result) - 1, 2) as tailResult,
                f.prizeId,
                now() as createdAt,
                count(f.prizeId) as frequency
        from fact_prize_results f
        group by f.prizeId, f.result
    """)
    void statisticPairFrequencies();

    @SqlUpdate("""
        insert into agg_region_result_freq (
            regionId, prizeId, result, createdAt, frequency
        ) 
        select c.regionId, f.prizeId, f.result, now() as createdAt, count(f.prizeId) as frequency
        from fact_prize_results f join dim_company c on f.companyId = c.id
        group by f.prizeId, f.result, c.regionId
    """)
    void statisticRegionResultFrequencies();
}
