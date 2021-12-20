package jp.co.canon.rss.logmanager.dto.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class AddressBookDTO {
	private Long id;
	private String name;
	private String email;
	private boolean group;
}
