package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemoteJobRepository extends CrudRepository<RemoteJobVo, Integer> {
    List<RemoteJobVo> findBy(Sort sort);
    Optional<RemoteJobVo> findByJobId(int jobId);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set stop = :stop where id = :id", nativeQuery = true)
    void updateStopStatus(@Param("id") int id, @Param("stop") Boolean stop);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set collect_status = :collect_status where id = :id", nativeQuery = true)
    void updateCollectStatus(@Param("id") int id, @Param("collect_status") String collect_status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set collect_error = :collect_error where id = :id", nativeQuery = true)
    void updateCollectError(@Param("id") int id, @Param("collect_error") String collect_error);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set convert_status = :convert_status where id = :id", nativeQuery = true)
    void updateConvertStatus(@Param("id") int id, @Param("convert_status") String convert_status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set convert_error = :convert_error where id = :id", nativeQuery = true)
    void updateConvertError(@Param("id") int id, @Param("convert_error") String convert_error);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set error_summary_status = :error_summary_status where id = :id", nativeQuery = true)
    void updateErrorSummaryStatus(@Param("id") int id, @Param("error_summary_status") String error_summary_status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set error_summary_error = :error_summary_error where id = :id", nativeQuery = true)
    void updateErrorSummaryError(@Param("id") int id, @Param("error_summary_error") String error_summary_error);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set cras_status = :cras_status where id = :id", nativeQuery = true)
    void updateCrasStatus(@Param("id") int id, @Param("cras_status") String cras_status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set cras_error = :cras_error where id = :id", nativeQuery = true)
    void updateCrasError(@Param("id") int id, @Param("cras_error") String cras_error);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set version_check_status = :version_check_status where id = :id", nativeQuery = true)
    void updateVersionCheckStatus(@Param("id") int id, @Param("version_check_status") String version_check_status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set version_check_error = :version_check_error where id = :id", nativeQuery = true)
    void updateVersionCheckError(@Param("id") int id, @Param("version_check_error") String version_check_error);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set db_purge_status = :db_purge_status where id = :id", nativeQuery = true)
    void updateDbPurgeStatus(@Param("id") int id, @Param("db_purge_status") String db_purge_status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.remotejob set db_purge_error = :db_purge_error where id = :id", nativeQuery = true)
    void updateDbPurgeError(@Param("id") int id, @Param("db_purge_error") String db_purge_error);
}
