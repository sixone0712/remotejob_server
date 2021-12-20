package jp.co.canon.rss.logmanager.dto.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReqDeleteAddressDto {
	@NotNull
	List<Long> emailIds;
}
