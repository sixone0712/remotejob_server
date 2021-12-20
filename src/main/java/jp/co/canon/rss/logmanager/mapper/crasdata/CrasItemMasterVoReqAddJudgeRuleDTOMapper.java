package jp.co.canon.rss.logmanager.mapper.crasdata;

import jp.co.canon.rss.logmanager.dto.rulecrasdata.ReqAddJudgeRuleDTO;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataSiteVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CrasItemMasterVoReqAddJudgeRuleDTOMapper {
    CrasItemMasterVoReqAddJudgeRuleDTOMapper INSTANCE = Mappers.getMapper(CrasItemMasterVoReqAddJudgeRuleDTOMapper.class);

    @Mapping(target="crasDataSiteVoItem", expression = "java(mapSiteVo(siteId))")
    @Mapping(target="siteId", expression = "java(mapSiteId(siteId))")
    @Mapping(target="userName", expression = "java(mapUserName(crasDataSiteVo))")
    @Mapping(target="fabName", expression = "java(mapFabName(crasDataSiteVo))")
    CrasItemMasterVo mapReqAddCrasJudgeRuleDtoToVo(ReqAddJudgeRuleDTO reqAddJudgeRuleDTO, int siteId, CrasDataSiteVo crasDataSiteVo);

    default CrasDataSiteVo mapSiteVo(int siteId) {
        CrasDataSiteVo crasDataSiteVo = new CrasDataSiteVo()
                .setId(siteId);
        return crasDataSiteVo;
    }

    default int mapSiteId(int siteId) {
        int mapSiteId = siteId;
        return mapSiteId;
    }

    default String mapUserName(CrasDataSiteVo crasDataSiteVo) {
        return crasDataSiteVo.getCrasDataSiteVo().getCrasCompanyName();
    }

    default String mapFabName(CrasDataSiteVo crasDataSiteVo) {
        return crasDataSiteVo.getCrasDataSiteVo().getCrasFabName();
    }

    default CrasItemMasterVo mapReqEditJudgeRuleDtoToVo(CrasItemMasterVo crasItemMasterVo, ReqAddJudgeRuleDTO reqAddJudgeRuleDTO, int crasDataSiteId, CrasDataSiteVo crasDataSiteVo) {
        crasItemMasterVo.setItemName(reqAddJudgeRuleDTO.getItemName())
                .setTitle(reqAddJudgeRuleDTO.getTitle())
                .setDescription(reqAddJudgeRuleDTO.getDescription())
                .setCalRange(reqAddJudgeRuleDTO.getCalRange())
                .setCalCondition(reqAddJudgeRuleDTO.getCalCondition())
                .setThreshold(reqAddJudgeRuleDTO.getThreshold())
                .setCompare(reqAddJudgeRuleDTO.getCompare())
                .setEnable(reqAddJudgeRuleDTO.getEnable())
                .setUserName(crasDataSiteVo.getCrasDataSiteVo().getCrasCompanyName())
                .setFabName(crasDataSiteVo.getCrasDataSiteVo().getCrasFabName());
        return crasItemMasterVo;
    }
}
