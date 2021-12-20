package jp.co.canon.rss.logmanager.repository;


import jp.co.canon.rss.logmanager.vo.MailContextVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailContextRepository extends JpaRepository<MailContextVo, Integer> {
}
