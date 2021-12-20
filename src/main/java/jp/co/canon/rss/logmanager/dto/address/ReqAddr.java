package jp.co.canon.rss.logmanager.dto.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReqAddr {
	@NotNull
	String name;

	@NotNull
	@Email
	String email;
}
