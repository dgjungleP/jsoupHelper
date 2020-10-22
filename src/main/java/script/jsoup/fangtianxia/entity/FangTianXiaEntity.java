package script.jsoup.fangtianxia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.cglib.beans.BeanCopier;

import java.net.URL;


/**
 * @version: v1.0
 * @date: 2020/10/19
 * @author: dgj
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FangTianXiaEntity {
    Long id;
    String houseName;
    URL url;
    EntityInfo entityInfo;

    public FangTianXiaEntity copy() {
        FangTianXiaEntity fangTianXiaEntity = new FangTianXiaEntity();
        BeanCopier copier = BeanCopier.create(FangTianXiaEntity.class, FangTianXiaEntity.class, false);
        copier.copy(this, fangTianXiaEntity, null);
        return fangTianXiaEntity;
    }

    public FangTianXiaRepos createRepo() {
        FangTianXiaRepos fangTianXiaRepos = new FangTianXiaRepos();
        fangTianXiaRepos.houseName = this.houseName;
        fangTianXiaRepos.url = this.url.getPath();
        return fangTianXiaRepos;
    }
}
