package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import liuyuyang.net.enums.article.ArticleStatusEnum;
import liuyuyang.net.validation.ValidationGroups;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@TableName("article_config")
public class ArticleConfig {
    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "文章状态：1 正常，2 首页隐藏，3 全站隐藏", example = "1", allowableValues = "1, 2, 3")
    @NotNull(message = "文章状态不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private ArticleStatusEnum status;

    @Schema(description = "文章密码", example = "默认为空表示不加密")
    @Size(max = 50, message = "文章密码不能超过50个字符", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String password;

    @Schema(description = "是否为文章草稿", example = "false")
    private Boolean isDraft;

    @Schema(description = "是否为加密文章", example = "false")
    private Boolean isEncrypt;

    @Schema(description = "是否删除（可恢复）", example = "false")
    private Boolean isDel;

    @Schema(description = "文章ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer articleId;
}
