package DAO;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface ControlDAO {
   @SqlQuery("""
           SELECT check_transform_dw_dependent_process(:processId)
           """)
    boolean checkTransformDWDependentProcess(@Bind("processId") int processId);

    @SqlQuery("""
           SELECT check_completed_process(:processId)
           """)
    boolean checkCompletedProcess(@Bind("processId") int processId);

   @SqlQuery("""
           CALL start_process(:processId)
           """)
   int startLoadProcess(@Bind("processId") int processId);

   @SqlCall("""
           CALL record_log(:logId, :message, :status)
           """)
    void recordLoadDWLog(@Bind("logId") int logId, @Bind("message") String message, @Bind("status") int status);

   @SqlQuery("""
           CALL get_source_path_load(:processId)
           """)
    String getSourcePath(@Bind("processId") int processId);

   @SqlQuery("""
           SELECT CAST(AES_DECRYPT(:encoderPassword, :key) AS CHAR(255)) AS ssn_decrypted
           """)
    String getDecoderPassword(@Bind("encoderPassword") String encoderPassword, @Bind("key") String key);

}
