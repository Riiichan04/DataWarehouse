package DAO;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;

public interface ControlDAO {
   @SqlCall("""
           CALL check_transform_dw_dependent_process()
           """)
    boolean checkTransformDWDependentProcess();

   @SqlCall("""
           CALL record_transform_dw_log(:log, :status)
           """)
    void recordTransformDWLog(@Bind("log") String log, @Bind("status") int status);

   @SqlCall("""
           CALL get_export_dir_transform_dw()
           """)
    String getExportDirTransformDw();
}
