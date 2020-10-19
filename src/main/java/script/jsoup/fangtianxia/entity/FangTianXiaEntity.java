package script.jsoup.fangtianxia.entity;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

/**
 * @version: v1.0
 * @date: 2020/10/19
 * @author: dgj
 */

@Data
@Builder
public class FangTianXiaEntity {
    String id;
    String houseName;
    URL url;
    EntityInfo info;
}
