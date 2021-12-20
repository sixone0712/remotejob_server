package jp.co.canon.rss.logmanager.vo.crasdata;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "cras_item_master", schema = "cras")
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CrasItemMasterVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer itemId;
    @Column(name = "site_id", nullable = false)
    private int siteId;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "fab_name", nullable = false)
    private String fabName;
    @Column(name = "item_name", nullable = false)
    private String itemName;
    @Column(name = "calc_range")
    private Double calRange;
    @Column(name = "condition")
    private String calCondition;
    @Digits(integer=12, fraction=3)
    @Column(name = "threshold")
    private Double threshold;
    @Column(name = "compare")
    private String compare;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "enable", nullable = false)
    private Boolean enable;
    @Column(name = "unit")
    private String unit;

    @ManyToOne
    @JoinColumn(name = "cras_data_item_site_id_fk")
    private CrasDataSiteVo crasDataSiteVoItem;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "crasItemMasterVo")
    private List<CrasItemMasterJobVo> crasItemMasterJobVoList = new ArrayList<>();
}
