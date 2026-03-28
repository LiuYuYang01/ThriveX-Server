package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.core.enums.ArticleStatus;
import lombok.Data;

@Data
@TableName("article_config")
public class ArticleConfig {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "文章状态：1 正常，2 首页隐藏，3 全站隐藏", example = "1", allowableValues = "1, 2, 3")
    private ArticleStatus status;

    @ApiModelProperty(value = "文章密码", example = "默认为空表示不加密")
    private String password;

    @ApiModelProperty(value = "是否为文章草稿", example = "默认：0，草稿：1")
    private Integer isDraft;

    @ApiModelProperty(value = "是否为加密文章", example = "默认：0，加密：1")
    private Integer isEncrypt;

    @ApiModelProperty(value = "是否严格删除", example = "默认：0，严格删除：1")
    private Integer isDel;

    @ApiModelProperty(value = "文章ID", example = "1", required = true)
    private Integer articleId;
}
