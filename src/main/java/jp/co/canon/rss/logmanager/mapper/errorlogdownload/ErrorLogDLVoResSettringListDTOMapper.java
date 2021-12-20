package jp.co.canon.rss.logmanager.mapper.errorlogdownload;

import jp.co.canon.rss.logmanager.dto.errorlogdownload.ResSettingListDTO;
import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadSettingVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ErrorLogDLVoResSettringListDTOMapper {
    ErrorLogDLVoResSettringListDTOMapper INSTANCE = Mappers.getMapper(ErrorLogDLVoResSettringListDTOMapper.class);

    @Mapping(target="error_code_range", expression = "java(mapErrorCode(errorLogDownloadSettingVo))")
    ResSettingListDTO mapErrorLogDlVoToDto(ErrorLogDownloadSettingVo errorLogDownloadSettingVo);

    default String mapErrorCode(ErrorLogDownloadSettingVo errorLogDownloadSettingVo) {
        return errorLogDownloadSettingVo.getError_code();
    }
}
