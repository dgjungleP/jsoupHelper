package script.jsoup.fangtianxia.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @version: v1.0
 * @date: 2020/10/22
 * @author: dgj
 */
@Data
public class FangTianXiaRepos {
    @TableId(type = IdType.AUTO)
    Long id;
    String houseName;
    String url;
    Long entityInfoId;
}
