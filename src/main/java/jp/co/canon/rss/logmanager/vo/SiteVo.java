package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataSiteVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "site", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SiteVo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer siteId;
	@Column(name = "cras_company_name", nullable = false)
	private String crasCompanyName;
	@Column(name = "cras_fab_name", nullable = false)
	private String crasFabName;
	@Column(name = "cras_address", nullable = false)
	private String crasAddress;
	@Column(name = "cras_port", nullable = false)
	private int crasPort;
	@Column(name = "rapidcollector_address", nullable = false)
	private String rssAddress;
	@Column(name = "rapidcollector_port", nullable = false)
	private int rssPort;
	@Column(name = "rapidcollector_user_name", nullable = false)
	private String rssUserName;
	@Column(name = "rapidcollector_password", nullable = false)
	private String rssPassword;
	@Column(name = "email_address", nullable = false)
	private String emailAddress;
	@Column(name = "email_port", nullable = false)
	private int emailPort;
	@Column(name = "email_user_name", nullable = false)
	private String emailUserName;
	@Column(name = "email_password", nullable = false)
	private String emailPassword;
	@Column(name = "email_from", nullable = false)
	private String emailFrom;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "siteVoList")
	private List<RemoteJobVo> remoteJobVo = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "siteVoListLocal")
	private List<LocalJobVo> localJobVo = new ArrayList<>();

	@OneToOne(mappedBy="crasDataSiteVo", orphanRemoval = true)
	private CrasDataSiteVo crasDataSiteVo;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "siteVoList")
	private List<ErrorLogDownloadSettingVo> errorLogDownloadSettingVoList = new ArrayList<>();
}


