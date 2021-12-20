package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.HistoryVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryVo, Integer> {
    List<HistoryVo> findByRequestId(String requestId, Sort sort);
    List<HistoryVo> findByJobId(int jobId, Sort sort);
    HistoryVo findByRequestIdCras(String requestIdCras);
    List<HistoryVo> findByJobIdAndType(int jobId, String type, Sort sort);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.history set status = :status where id = :id", nativeQuery = true)
    void updateHistoryStatus(@Param("id") int id, @Param("status") String status);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.history set ending_time = :endingTime where id = :id", nativeQuery = true)
    void updateHistoryEndingTime(@Param("id") int id, @Param("endingTime") String endingTime);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.history set request_id_cras = :requestIdCras where id = :id", nativeQuery = true)
    void updateRequestIdCras(@Param("id") int id, @Param("requestIdCras") String requestIdCras);
}
