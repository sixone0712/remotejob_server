package jp.co.canon.rss.logmanager.repository;


import jp.co.canon.rss.logmanager.vo.UserVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserVo, Integer> {
	UserVo findByUsername(String username);
}
