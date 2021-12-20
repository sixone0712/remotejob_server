package jp.co.canon.rss.logmanager.repository.crasdata;

import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterJobVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrasItemMasterJobRepository extends JpaRepository<CrasItemMasterJobVo, Integer> {
    List<CrasItemMasterJobVo> findByCrasRuleId(int crasRuleId);
    List<CrasItemMasterJobVo> findByCrasDataSiteId(int crasDataSiteId);
}
