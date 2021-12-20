package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ResLocalJobListDTO;
import jp.co.canon.rss.logmanager.vo.LocalJobVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocalJobVoResLocalJobListDtoMapper {
    LocalJobVoResLocalJobListDtoMapper INSTANCE = Mappers.getMapper(LocalJobVoResLocalJobListDtoMapper.class);

    default List<ResLocalJobListDTO> mapResLocalJobListVoToDto(List<LocalJobVo> localJobVoList) {
        List<ResLocalJobListDTO> resLocalJobListDTOList = new ArrayList<>();

        for(LocalJobVo localJobVo : localJobVoList) {
            ResLocalJobListDTO resLocalJobListDTO = new ResLocalJobListDTO(
                    localJobVo.getJobId(),
                    localJobVo.getSiteId(),
                    localJobVo.getSiteVoListLocal(),
                    localJobVo.getCollectStatus(),
                    localJobVo.getFileIndices(),
                    localJobVo.getFileOriginalNames(),
                    localJobVo.getRegisteredDate(),
                    localJobVo.isStop(),
                    localJobVo.getCollectError().equals("") ? new String[]{} : new String[] {localJobVo.getCollectError()}
            );
            resLocalJobListDTOList.add(resLocalJobListDTO);
        }
        return resLocalJobListDTOList;


    }
}
