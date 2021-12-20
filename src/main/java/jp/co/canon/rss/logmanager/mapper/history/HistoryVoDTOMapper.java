package jp.co.canon.rss.logmanager.mapper.history;

import jp.co.canon.rss.logmanager.dto.history.ReqHistoryDTO;
import jp.co.canon.rss.logmanager.dto.history.ResHistoryDTO;
import jp.co.canon.rss.logmanager.vo.HistoryVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoryVoDTOMapper {
    HistoryVoDTOMapper INSTANCE = Mappers.getMapper(HistoryVoDTOMapper.class);

    @Mapping(target="id", expression = "java(mapId(historyVo))")
    @Mapping(target="name", expression = "java(mapName(historyVo))")
    ResHistoryDTO mapResHistoryDTO(HistoryVo historyVo);

    default String mapId(HistoryVo historyVo) { return historyVo.getRequestIdCras(); }
    default String mapName(HistoryVo historyVo) { return historyVo.getRunningTime(); }
}
