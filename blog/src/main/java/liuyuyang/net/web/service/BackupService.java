package liuyuyang.net.web.service;

import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.model.BackupRecord;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BackupService {

    BackupRecord export();

    Map<String, Object> getBackupList(FilterDTO filterDTO);

    ResponseEntity<Resource> downloadBackupData(Integer id);

    void delBackupData(Integer id);

    /** 定时备份保留份数：仅保留最近 retainCount 份（含手动备份），0 或负数不清理 */
    void applyRetention(int retainCount);
}
