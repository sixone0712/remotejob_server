package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ResTimeLineDTO;
import jp.co.canon.rss.logmanager.vo.HistoryVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoryVoResTimeLineDtoMapper {
    HistoryVoResTimeLineDtoMapper INSTANCE = Mappers.getMapper(HistoryVoResTimeLineDtoMapper.class);

    @Mapping(target="start", expression = "java(mapVoString(historyVo.getSettingTime()))")
    @Mapping(target="runningStart", expression = "java(mapVoString(historyVo.getRunningTime()))")
    @Mapping(target="end", expression = "java(mapVoString(historyVo.getEndingTime()))")
    @Mapping(target="name", expression = "java(mapVoString(historyVo.getStep()))")
    @Mapping(target="logId", expression = "java(mapVoString(historyVo.getRequestIdCras()))")
    ResTimeLineDTO mapHistoryVotoDto(HistoryVo historyVo);

    default String mapVoString(String voString) {
        return voString;
    }
}
