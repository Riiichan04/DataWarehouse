package dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;

public interface ControlDAO {
    //Insert new process
    @SqlUpdate("""
                call insert_extract_process(
                    :name, :description, :scriptName, :typeProcess, :sourceDbId, :sourceTableName, :targetPath
                )
            """)
    @GetGeneratedKeys
    int insertProcess(
            @Bind("name") String name,
            @Bind("description") String description,
            @Bind("scriptName") String scriptName,
            @Bind("typeProcess") String typeProcess,
            @Bind("sourceDbId") int sourceDbId,
            @Bind("sourceTableName") String sourceTableName,
            @Bind("targetPath") String targetPath
    );


    //Insert log
    @SqlUpdate("""
                insert into log_process(processId, startTime, endTime, status, message, createdAt, updatedAt)
                value(:processId, :startTime, :endTime, :status, :message, now(), now())
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
