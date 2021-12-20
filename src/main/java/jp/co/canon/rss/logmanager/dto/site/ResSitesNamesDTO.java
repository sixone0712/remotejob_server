package jp.co.canon.rss.logmanager.dto.site;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResSitesNamesDTO implements Comparable {
    private Integer siteId;
    private String crasCompanyName;
    private String crasFabName;

    public ResSitesNamesDTO(Integer siteId, String crasCompanyName, String crasFabName) {
        this.siteId = siteId;
        this.crasCompanyName = crasCompanyName;
        this.crasFabName = crasFabName;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof ResSitesNamesDTO))
            return false;
        ResSitesNamesDTO p = (ResSitesNamesDTO) o;
        return p.siteId.equals(siteId) && p.crasCompanyName.equals(crasCompanyName) && p.crasFabName.equals(crasFabName);
    }

    @Override
    public int compareTo(Object obj) {
        ResSitesNamesDTO s2 = (ResSitesNamesDTO)obj;

        return s2.siteId - this.siteId;
    }
}
