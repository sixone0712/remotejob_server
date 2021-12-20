package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.dto.site.ResSitesDetailDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesNamesDTO;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<SiteVo, Integer>  {
    List<ResSitesNamesDTO> findBy();
    List<ResSitesDetailDTO> findBy(Sort sort);
    Optional<ResSitesDetailDTO> findBySiteId(int siteId);
    Optional<ResSitesDetailDTO> findByCrasCompanyNameIgnoreCaseAndCrasFabNameIgnoreCase(String siteCompanyName, String siteFabName);
}
