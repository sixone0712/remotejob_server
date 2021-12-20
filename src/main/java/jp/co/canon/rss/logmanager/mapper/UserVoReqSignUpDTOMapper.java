package jp.co.canon.rss.logmanager.mapper;

import jp.co.canon.rss.logmanager.dto.auth.ReqSingUpDTO;
import jp.co.canon.rss.logmanager.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserVoReqSignUpDTOMapper extends GenericMapper<ReqSingUpDTO, UserVo> {
	UserVoReqSignUpDTOMapper INSTANCE = Mappers.getMapper(UserVoReqSignUpDTOMapper.class);
}