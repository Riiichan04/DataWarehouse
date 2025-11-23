package dao;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface AggregateDAO {
    @SqlUpdate("""
        truncate table aggregate_historical_prizes
    """)
    void truncateAggHistoryPrizes();

    @SqlUpdate("""
        truncate table aggregate_result_frequencies
    """)
    void truncateAggResultFrequencies();

    @SqlUpdate("""
        truncate table aggregate_pair_frequencies
    """)
    void truncateAggPairFrequencies();

    @SqlUpdate("""
        insert into aggregate_historical_prizes (
            prizeId, result, resultCount, companyName
        )
        select f.prizeId, f.result, count(prizeId) as resultCount, c.name as companyName
        from fact_prize_results f join dim_company c on f.companyId = c.id
        group by f.prizeId, f.result, c.name
    """)
    void statisticMostAppearReward();

    @SqlUpdate("""
        insert into aggregate_result_frequencies (
            result, resultCount, numberOfWeek, companyName
        )
        select f.result, count(prizeId) as resultCount, d.numberOfWeek, c.name as companyName
        from fact_prize_results f join dim_date d on f.dateId = d.id join dim_company c on f.companyId = c.id
        group by f.prizeId, f.result, d.numberOfWeek, c.name
    """)
    void statisticResultFrequencies();

    @SqlUpdate("""
        insert into aggregate_pair_frequencies (
            prizeId, prizePair, resultCount, companyName
        )
        select
                f.prizeId,
                substring(f.result, length(f.result) - 1, 2) as prizePair,
                count(prizeId) as resultCount,
                c.name as companyName
        from fact_prize_results f join dim_company c on f.companyId = c.id
        group by f.prizeId, f.result, c.name
    """)
    void statisticPairFrequencies();
}
