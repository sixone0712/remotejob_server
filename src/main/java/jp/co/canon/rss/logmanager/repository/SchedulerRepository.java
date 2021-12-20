package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.SchedulerVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerVo, Integer> {
    List<SchedulerVo> findByJobId(int jobId);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.scheduler set setting_time = :settingTime where id = :id", nativeQuery = true)
    void updateSettingTime(@Param("id") int id, @Param("settingTime") String settingTime);
}
