package jp.co.canon.rss.logmanager.mapper.crasdata;

import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataDetailDTO;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CrasDataVoResCrasDataDetailDTOMapper {
    CrasDataVoResCrasDataDetailDTOMapper INSTANCE = Mappers.getMapper(CrasDataVoResCrasDataDetailDTOMapper.class);

    @Mapping(target="targetCol", expression = "java(mapTargetCol(crasDataVo))")
    ResCrasDataDetailDTO mapResCrasDataDetailDTO(CrasDataVo crasDataVo);

    default int mapItemId(int itemId) { return itemId; }
    default String [] mapTargetCol(CrasDataVo crasDataVo) {
        List<String> targetColList = new ArrayList<>();
        if(crasDataVo.getTargetCol2().length()==0)
            targetColList.add(crasDataVo.getTargetCol1());
        else {
            targetColList.add(crasDataVo.getTargetCol1());
            targetColList.add(crasDataVo.getTargetCol2());
        }
        String [] targetCol = targetColList.toArray(new String[targetColList.size()]);
        return targetCol;
    }
}
