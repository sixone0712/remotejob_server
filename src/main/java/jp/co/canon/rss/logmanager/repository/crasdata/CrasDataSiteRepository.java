package jp.co.canon.rss.logmanager.repository.crasdata;

import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataSiteVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrasDataSiteRepository extends JpaRepository<CrasDataSiteVo, Integer> {
    List<CrasDataSiteVo> findBy(Sort sort);
    CrasDataSiteVo findBySiteId(int siteId);
}
