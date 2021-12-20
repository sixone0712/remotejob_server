package jp.co.canon.rss.logmanager.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@ToString
@Accessors(chain = true)
public class ReqChangeRoleDTO {
	@NotNull
	private List<String> roles;
}
