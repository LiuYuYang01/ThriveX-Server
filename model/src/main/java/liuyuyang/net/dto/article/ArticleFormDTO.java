package liuyuyang.net.dto.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.ArticleConfig;
import lombok.Data;

import java.util.List;

@Data
public class ArticleFormDTO {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文章标题", example = "示例文章标题", required = true)
    private String title;

    @ApiModelProperty(value = "文章介绍", example = "示例文章介绍")
    private String description;

    @ApiModelProperty(value = "文章主要内容", example = "示例文章内容", required = true)
    private String content;

    @ApiModelProperty(value = "文章封面链接", example = "http://123.com/images/example.jpg")
    private String cover;

    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的分类ID", example = "1,2,3")
    private List<Integer> cateIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的标签ID", example = "1,2,3")
    private List<Integer> tagIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "文章配置项")
    private ArticleConfig config;

    @ApiModelProperty(value = "创建时间", example = "1723533206613", required = true)
    private String createTime;
}
