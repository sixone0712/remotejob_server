package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import jp.co.canon.rss.logmanager.vo.address.JobAddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.JobGroupBookEntity;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterJobVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "mail_context", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MailContextVo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Type(type = "string-array")
	@Column(name = "custom_emails", columnDefinition = "text []")
	private String [] customEmails;
	@Column(name = "subject", columnDefinition = "TEXT")
	private String subject;
	@Column(name = "body", columnDefinition = "TEXT")
	private String body;
	@Column(name = "before")
	private int before;
	@Column(name = "mode")	// time, cycle
	private String mode;
	@Type(type = "string-array")
	@Column(name = "time", columnDefinition = "text []")
	private String [] time;
	@Column(name = "cycle")	// day, minute, hour
	private String cycle;
	@Column(name = "period")
	private int period;
	@Column(name = "preScript", columnDefinition = "text")
	private String preScript;
	@Column(name = "nextScript", columnDefinition = "text")
	private String nextScript;

	@OneToMany(mappedBy = "mailContext", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<JobAddressBookEntity> address = new HashSet<>();

	@OneToMany(mappedBy = "mailContext", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<JobGroupBookEntity> group = new HashSet<>();

	@OneToMany(mappedBy = "mailContext", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<CrasItemMasterJobVo> crasItem = new HashSet<>();
}
