package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.address.JobAddressBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JobAddressBookRepository extends JpaRepository<JobAddressBookEntity, Long> {

	@Transactional
	@Modifying
	@Query("delete from JobAddressBookEntity job where job.address.id in (:email_ids)")
	void deleteAllByGroupIds(@Param("email_ids") List<Long> ids);
}
