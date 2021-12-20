package jp.co.canon.rss.logmanager.vo.address;

import jp.co.canon.rss.logmanager.vo.MailContextVo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "mail_context_group")
@Getter
@Setter
@Accessors(chain = true)
public class JobGroupBookEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private GroupBookEntity group;

	@ManyToOne
	@JoinColumn(name = "mail_context_id")
	private MailContextVo mailContext;
}
