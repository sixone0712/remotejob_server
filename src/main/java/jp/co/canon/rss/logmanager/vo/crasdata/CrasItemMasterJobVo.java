package jp.co.canon.rss.logmanager.vo.crasdata;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jp.co.canon.rss.logmanager.vo.MailContextVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "cras_item_master_job", schema = "cras")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CrasItemMasterJobVo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "cras_data_site_id", nullable = false)
	private int crasDataSiteId;
	@Column(name = "cras_rule_id", nullable = false)
	private int crasRuleId;

	@ManyToOne
	@JoinColumn(name = "cras_item_master_id")
	private CrasItemMasterVo crasItemMasterVo;

	@ManyToOne
	@JoinColumn(name = "mail_context_id")
	private MailContextVo mailContext;
}


