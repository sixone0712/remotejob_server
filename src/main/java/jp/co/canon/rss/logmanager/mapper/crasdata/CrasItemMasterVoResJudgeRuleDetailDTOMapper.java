package jp.co.canon.rss.logmanager.mapper.crasdata;

import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResJudgeRuleDetailDTO;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CrasItemMasterVoResJudgeRuleDetailDTOMapper {
    CrasItemMasterVoResJudgeRuleDetailDTOMapper INSTANCE = Mappers.getMapper(CrasItemMasterVoResJudgeRuleDetailDTOMapper.class);

    ResJudgeRuleDetailDTO mapResJudgeRuleDetailDTO(CrasItemMasterVo crasItemMasterVo);

    default int mapItemId(int itemId) { return itemId; }
}
