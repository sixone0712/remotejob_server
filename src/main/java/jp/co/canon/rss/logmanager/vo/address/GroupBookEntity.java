package jp.co.canon.rss.logmanager.vo.address;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "group_book", schema = "log_manager")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class GroupBookEntity {

	public GroupBookEntity(String name, AddressBookEntity address) {
		this.name = name;
		this.address = address;
	}

	@Id
	@GenericGenerator(name = "groupIdGen", strategy = "jp.co.canon.rss.logmanager.vo.address.AddressIdGenerator",
		parameters = {@org.hibernate.annotations.Parameter(name = "target", value = "group")})
	@GeneratedValue(generator = "groupIdGen")
	@Column(name = "gid", nullable = false)
	private Long gid;

	@Column(name = "name")
	private String name;

	@ManyToOne(cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "id")
	private AddressBookEntity address;

//	@OneToMany(mappedBy = "mailContext", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//	private List<JobGroupBookEntity> mailContext = new ArrayList<>();
}
