package jp.co.canon.rss.logmanager.manager;

import jp.co.canon.rss.logmanager.repository.BlockedTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class PurgeManager {

	private BlockedTokenRepository blockedTokenRepository;

	public PurgeManager(BlockedTokenRepository blockedTokenRepository) {
		this.blockedTokenRepository = blockedTokenRepository;
	}

	@Scheduled(cron = "${auth.jwt.delete-scheduled}")
	public void cronExpiredBlockedToken() {

		try {
			log.info("[cronExpiredBlockedToken] Delete expired blocking tokens");
			blockedTokenRepository.deleteAllByExpiredBefore(LocalDateTime.now());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
