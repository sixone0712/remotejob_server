package jp.co.canon.rss.logmanager.mapper.status;

import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobListDTO;
import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RemoteJobVoResRemoteJobListDtoMapper {
    RemoteJobVoResRemoteJobListDtoMapper INSTANCE = Mappers.getMapper(RemoteJobVoResRemoteJobListDtoMapper.class);

    @Mapping(target="mpaVersionError", expression = "java(mapErrorToArray(remoteJobVo.getMpaVersionError()))")
    @Mapping(target="crasDataError", expression = "java(mapErrorToArray(remoteJobVo.getCrasDataError()))")
    @Mapping(target="errorSummaryError", expression = "java(mapErrorToArray(remoteJobVo.getErrorSummaryError()))")
    @Mapping(target="collectError", expression = "java(mapErrorToArray(remoteJobVo.getCollectError()))")
    @Mapping(target="companyName", expression = "java(mapName(remoteJobVo.getSiteVoList().getCrasCompanyName()))")
    @Mapping(target="fabName", expression = "java(mapName(remoteJobVo.getSiteVoList().getCrasFabName()))")
    ResRemoteJobListDTO mapRemoteJobVoToDto(RemoteJobVo remoteJobVo);

    default String [] mapErrorToArray(String collectError) {
        List<String> detailArrayList = new ArrayList<>();
        String [] result = new String[detailArrayList.size()];
        if(collectError==null || collectError.equals("")) {
            return result;
        }
        else {
            detailArrayList = new ArrayList<>();
            detailArrayList.add(collectError);
            return detailArrayList.toArray(new String[detailArrayList.size()]);
        }
    }

    default String mapName(String name) { return name; }
}
