package jp.co.canon.rss.logmanager.repository;


import jp.co.canon.rss.logmanager.vo.BlockedTokenVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface BlockedTokenRepository extends JpaRepository<BlockedTokenVo, Integer> {
	BlockedTokenVo findByToken(String name);

	BlockedTokenVo findByUuid(UUID uuid);

	@Transactional
	void deleteAllByExpiredBefore(LocalDateTime expired);
}
