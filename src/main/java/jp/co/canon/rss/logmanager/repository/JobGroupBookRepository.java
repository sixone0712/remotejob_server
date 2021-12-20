package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.address.JobGroupBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JobGroupBookRepository extends JpaRepository<JobGroupBookEntity, Long> {

	@Transactional
	@Modifying
	@Query("delete from JobGroupBookEntity job where job.group.gid = :group_id")
	void deleteAllByGroupId(@Param("group_id") Long id);
}
