package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadStatusVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErrorLogDownloadStatusRepository extends JpaRepository<ErrorLogDownloadStatusVo, Integer> {
    List<ErrorLogDownloadStatusVo> findBySiteId(int siteId, Sort sort);
    Optional<ErrorLogDownloadStatusVo> findByRid(String rid);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.error_log_download_status set download_url = :download_url where rid = :rid", nativeQuery = true)
    void updateDownloadUrl(@Param("rid") String rid, @Param("download_url") String download_url);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.error_log_download_status set error = :error where rid = :rid", nativeQuery = true)
    void updateError(@Param("rid") String rid, @Param("error") String error);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.error_log_download_status set status = :status where rid = :rid", nativeQuery = true)
    void updateStatus(@Param("rid") String rid, @Param("status") String status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.error_log_download_status set saved_file_name = :saved_file_name where rid = :rid", nativeQuery = true)
    void updateSavedFileName(@Param("rid") String rid, @Param("saved_file_name") String saved_file_name);
}
