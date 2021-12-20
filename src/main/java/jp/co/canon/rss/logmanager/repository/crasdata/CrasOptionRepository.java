package jp.co.canon.rss.logmanager.repository.crasdata;

import jp.co.canon.rss.logmanager.vo.crasdata.CrasOptionVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrasOptionRepository extends JpaRepository<CrasOptionVo, Integer> {
    List<CrasOptionVo> findByCras(String cras);
}
