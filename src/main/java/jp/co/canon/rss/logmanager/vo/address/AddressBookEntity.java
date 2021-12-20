package jp.co.canon.rss.logmanager.vo.address;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "address_book", schema = "log_manager")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class AddressBookEntity {

	public AddressBookEntity(String name, String email) {
		this.name = name;
		this.email = email;
	}

	@Id
	@GenericGenerator(name = "addrIdGen", strategy = "jp.co.canon.rss.logmanager.vo.address.AddressIdGenerator",
		parameters = {@org.hibernate.annotations.Parameter(name = "target", value = "address")})
	@GeneratedValue(generator = "addrIdGen")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

//	@OneToMany(mappedBy = "mailContext", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//	private List<JobAddressBookEntity> mailContext = new ArrayList<>();

}
