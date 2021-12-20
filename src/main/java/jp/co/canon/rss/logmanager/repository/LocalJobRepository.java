package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.dto.job.ResLocalJobListDTO;
import jp.co.canon.rss.logmanager.vo.LocalJobVo;
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
public interface LocalJobRepository extends JpaRepository<LocalJobVo, Integer> {
    List<LocalJobVo> findBy(Sort sort);
    Optional<LocalJobVo> findByJobId(int jobId);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.localjob set stop = :stop where id = :id", nativeQuery = true)
    void updateStopStatus(@Param("id") int id, @Param("stop") Boolean stop);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.localjob set collect_status = :collect_status where id = :id", nativeQuery = true)
    void updateConvertStatus(@Param("id") int id, @Param("collect_status") String collect_status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.localjob set collect_error = :collect_error where id = :id", nativeQuery = true)
    void updateConvertError(@Param("id") int id, @Param("collect_error") String collect_error);
}
