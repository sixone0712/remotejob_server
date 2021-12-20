package jp.co.canon.rss.logmanager.mapper.errorlogdownload;

import jp.co.canon.rss.logmanager.dto.errorlogdownload.ResErrorLogDownloadStatusDTO;
import jp.co.canon.rss.logmanager.mapper.GenericMapper;
import jp.co.canon.rss.logmanager.vo.ErrorLogDownloadStatusVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ErrorLogDLStatusVoResErrorLogDLStatusDTOMapper extends GenericMapper<ResErrorLogDownloadStatusDTO, ErrorLogDownloadStatusVo> {
    ErrorLogDLStatusVoResErrorLogDLStatusDTOMapper INSTANCE = Mappers.getMapper(ErrorLogDLStatusVoResErrorLogDLStatusDTOMapper.class);
}
