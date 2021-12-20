package jp.co.canon.rss.logmanager.mapper.crasdata;

import jp.co.canon.rss.logmanager.dto.rulecrasdata.ReqAddCrasDataDTO;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataSiteVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CrasDataVoReqAddCrasDataDTOMapper {
    CrasDataVoReqAddCrasDataDTOMapper INSTANCE = Mappers.getMapper(CrasDataVoReqAddCrasDataDTOMapper.class);

    @Mapping(target="targetCol1", expression = "java(mapTargetCol(reqAddCrasDataDTO, 0))")
    @Mapping(target="targetCol2", expression = "java(mapTargetCol(reqAddCrasDataDTO, 1))")
    @Mapping(target="crasDataSiteVo", expression = "java(mapSiteVo(siteId))")
    @Mapping(target="siteId", expression = "java(mapSiteId(siteId))")
    @Mapping(target="userName", expression = "java(mapUserName(crasDataSiteVo))")
    @Mapping(target="fabName", expression = "java(mapFabName(crasDataSiteVo))")
    CrasDataVo mapReqAddCrasDataDtoToVo(ReqAddCrasDataDTO reqAddCrasDataDTO, int siteId, CrasDataSiteVo crasDataSiteVo);

    default String mapTargetCol(ReqAddCrasDataDTO reqAddCrasDataDTO, int idx) {
        String targetCol = null;
        String[] targetColArray = reqAddCrasDataDTO.getTargetCol();
        if(reqAddCrasDataDTO.getTargetCol().length == 2) {
            targetCol = targetColArray[idx];
        }
        else {
            if (idx == 0)
                targetCol = targetColArray[idx];
            else if (idx == 1)
                targetCol = "";
        }
        return targetCol;
    }

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

    default CrasDataVo mapReqEditCrasDataDtoToVo(CrasDataVo crasDataVo, ReqAddCrasDataDTO reqAddCrasDataDTO, CrasDataSiteVo crasDataSiteVo) {
        String targetCol1 = mapTargetCol(reqAddCrasDataDTO, 0);
        String targetCol2 = mapTargetCol(reqAddCrasDataDTO, 1);

        crasDataVo.setItemName(reqAddCrasDataDTO.getItemName())
                .setEnable(reqAddCrasDataDTO.getEnable())
                .setTargetTable(reqAddCrasDataDTO.getTargetTable())
                .setTargetCol1(targetCol1)
                .setTargetCol2(targetCol2)
                .setComments(reqAddCrasDataDTO.getComments())
                .setOperations(reqAddCrasDataDTO.getOperations())
                .setCalPeriodUnit(reqAddCrasDataDTO.getCalPeriodUnit())
                .setCalResultType(reqAddCrasDataDTO.getCalResultType())
                .setCoef(reqAddCrasDataDTO.getCoef())
                .setManualWhere(reqAddCrasDataDTO.getManualWhere())
                .setUserName(crasDataSiteVo.getCrasDataSiteVo().getCrasCompanyName())
                .setFabName(crasDataSiteVo.getCrasDataSiteVo().getCrasFabName());
        return crasDataVo;
    }
}
