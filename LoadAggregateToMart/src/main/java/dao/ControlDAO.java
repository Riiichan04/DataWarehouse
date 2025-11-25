package dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;

public interface ControlDAO {
    //Insert log
    @SqlUpdate("""
                insert into log_process(processId, startTime, endTime, status, message, createdAt)
                value(:processId, :startTime, :endTime, :status, :message, now())
            """)
    @GetGeneratedKeys
    int insertLogProcess(
            @Bind("processId") int processId,
            @Bind("startTime") Timestamp startTime,
            @Bind("endTime") Timestamp endTime,
            @Bind("status") int status,
            @Bind("message") String message
    );
}