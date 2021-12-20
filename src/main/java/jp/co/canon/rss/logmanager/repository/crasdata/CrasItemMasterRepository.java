package jp.co.canon.rss.logmanager.repository.crasdata;

import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CrasItemMasterRepository extends JpaRepository<CrasItemMasterVo, Integer> {
    List<CrasItemMasterVo> findBySiteId(int siteId, Sort sort);
    long countBySiteId(int siteId);

//    @Query(value= "ALTER SEQUENCE cras.cras_item_master_id_seq RESTART WITH 1", nativeQuery= true)
//    @Modifying
//    @Transactional
//    void restarID();
}
