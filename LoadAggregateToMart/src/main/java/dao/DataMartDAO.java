package dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;

public interface DataMartDAO {
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
        truncate table dim_region
    """)
    void truncateDimRegion();

    @SqlUpdate("""
        insert into agg_prize_result_freq (
            prizeId, result, createdAt, frequency
        )
        values(:prizeId, :result, :createdAt, :frequency)
    """)
    void insertMostAppearReward(
            @Bind("prizeId") int prizeId,
            @Bind("result") String result,
            @Bind("createdAt") Timestamp createdAt,
            @Bind("frequency") int frequency
    );

    @SqlUpdate("""
        insert into agg_number_week_result_freq (
            result, numberOfWeek, createdAt, prizeId, frequency
        )
        values(:result, :numberOfWeek, :createdAt, :prizeId, :frequency)
    """)
    void insertResultFrequencies(
            @Bind("result") String result,
            @Bind("numberOfWeek") int numberOfWeek,
            @Bind("createdAt") Timestamp createdAt,
            @Bind("prizeId") int prizeId,
            @Bind("frequency") int frequency
    );

    @SqlUpdate("""
        insert into agg_tail_prize_freq (
            tailResult, prizeId, createdAt, frequency
        )
        values (:tailResult, :prizeId, :createdAt, :frequency)
    """)
    void insertPairFrequencies(
            @Bind("tailResult") String tailResult,
            @Bind("prizeId") int prizeId,
            @Bind("createdAt") Timestamp createdAt,
            @Bind("frequency") int frequency
    );

    @SqlUpdate("""
        insert into agg_region_result_freq (
            regionId, prizeId, result, createdAt, frequency
        )
        values (:regionId, :prizeId, :result, :createdAt, :frequency)
    """)
    void insertRegionResultFrequencies(
            @Bind("regionId") int regionId,
            @Bind("prizeId") int prizeId,
            @Bind("result") String result,
            @Bind("createdAt") Timestamp createdAt,
            @Bind("frequency") int frequency
    );

    @SqlUpdate("""
        insert into dim_region(codeRegion, name, description, createdAt, expiredAt)
        values(:codeRegion, :name, :description, :createdAt, :expiredAt)
    """)
    void insertToDimRegion(
            @Bind("codeRegion") String codeRegion,
            @Bind("name") String name,
            @Bind("description") String description,
            @Bind("createdAt") Timestamp createdAt,
            @Bind("expiredAt") Timestamp expiredAt
    );
}