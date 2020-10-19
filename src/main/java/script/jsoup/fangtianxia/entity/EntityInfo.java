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
public class EntityInfo {
    String price;
    String type;
    String address;
    String openInfo;
    URL baseInfoUrl;
    BaseInfo baseInfo;

}
