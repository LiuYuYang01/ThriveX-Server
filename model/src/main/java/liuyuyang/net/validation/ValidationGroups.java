package liuyuyang.net.validation;

/**
 * Bean Validation 分组：区分新增与编辑，避免公开接口与后台接口校验互相干扰。
 */
public final class ValidationGroups {

    public interface Create {
    }

    public interface Update {
    }

    private ValidationGroups() {
    }
}
