package jp.co.canon.rss.logmanager.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReqChangePasswordDTO {
	@NotNull
	private String currentPassword;

	@NotNull
	private String newPassword;
}
