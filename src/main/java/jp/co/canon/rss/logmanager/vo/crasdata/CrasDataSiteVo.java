package jp.co.canon.rss.logmanager.vo.crasdata;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "cras_data_site", schema = "cras")
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CrasDataSiteVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "siteId", nullable = false)
    private int siteId;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    @Column(name = "legacy", nullable = false)
    private Boolean legacy;

    @OneToOne
    @JoinColumn(name = "cras_data_site_vo_fk", unique = true)
    private SiteVo crasDataSiteVo;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "crasDataSiteVo")
    private List<CrasDataVo> crasDataVoList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "crasDataSiteVoItem")
    private List<CrasItemMasterVo> crasItemMasterVoList = new ArrayList<>();
}
