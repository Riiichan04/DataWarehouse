package DAO;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface ControlDAO {
   @SqlQuery("""
           SELECT check_transform_dw_dependent_process()
           """)
    boolean checkTransformDWDependentProcess();

   @SqlCall("""
           CALL start_process(:processId)
           """)
   int startTransformProcess(@Bind("processId") int processId);

   @SqlCall("""
           CALL record_transform_dw_log(:logId, :message, :status)
           """)
    void recordTransformDWLog(@Bind("logId") int logId, @Bind("message") String message, @Bind("status") int status);

   @SqlCall("""
           CALL get_export_dir_transform_dw(:processId)
           """)
    String getExportDirTransformDw(@Bind("processId") int processId);

}
