package jp.co.canon.rss.logmanager.repository.crasdata;

import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CrasDataRepository extends JpaRepository<CrasDataVo, Integer>  {
    List<CrasDataVo> findBySiteId(int siteId, Sort sort);
    long countBySiteId(int siteId);

//    @Query(value= "ALTER SEQUENCE cras.cras_data_id_seq RESTART WITH 1", nativeQuery= true)
//    @Modifying
//    @Transactional
//    void restarID();
}
