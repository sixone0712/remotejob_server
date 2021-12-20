package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.dto.errorlogdownload.ResSettingListDTO;
import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadSettingVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorLogDownloadSettingRepository extends JpaRepository<ErrorLogDownloadSettingVo, Integer> {
    List<ErrorLogDownloadSettingVo> findBySiteId(int siteId, Sort sort);
}
