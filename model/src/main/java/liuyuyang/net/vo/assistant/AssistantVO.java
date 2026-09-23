package liuyuyang.net.vo.assistant;

import liuyuyang.net.model.Assistant;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 保持返回 key：AI 助手由前端浏览器直连模型 API（Bearer key），key 不下发则功能不可用；
// 若改为后端代理 AI 请求，可在此摘掉 key
@Data
@EqualsAndHashCode(callSuper = true)
public class AssistantVO extends Assistant {

}
