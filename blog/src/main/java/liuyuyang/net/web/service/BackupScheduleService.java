package liuyuyang.net.web.service;

import liuyuyang.net.core.backup.BackupStorageRouter;
import liuyuyang.net.model.BackupRecord;
import liuyuyang.net.model.EnvConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定时备份调度：开关与 cron 每次触发后从 backup_storage 配置重读，后台改配置即时生效、无需重启；
 * 复用 {@link BackupService#export()}（记录 running → success/failed），失败仅记日志不中断调度。
 */
@Slf4j
@Service
public class BackupScheduleService implements SchedulingConfigurer {

    private final EnvConfigService envConfigService;
    private final BackupService backupService;
    // 手动备份与定时备份可能重叠，同一时刻只跑一个
    private final AtomicBoolean running = new AtomicBoolean();

    public BackupScheduleService(EnvConfigService envConfigService, BackupService backupService) {
        this.envConfigService = envConfigService;
        this.backupService = backupService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addTriggerTask(this::runScheduledBackup, this::nextExecution);
    }

    // 关闭时低频轮询配置以便重新开启；cron 非法时 1 分钟后重试，避免调度异常循环
    private Instant nextExecution(TriggerContext triggerContext) {
        ScheduleConfig config = readScheduleConfig();
        if (!config.enabled()) {
            return Instant.now().plus(Duration.ofMinutes(1));
        }
        try {
            Instant next = new CronTrigger(config.cron()).nextExecution(triggerContext);
            return next != null ? next : Instant.now().plus(Duration.ofMinutes(1));
        } catch (Exception e) {
            log.warn("定时备份 cron 配置非法：{}", config.cron());
            return Instant.now().plus(Duration.ofMinutes(1));
        }
    }

    private void runScheduledBackup() {
        ScheduleConfig config = readScheduleConfig();
        if (!config.enabled() || !running.compareAndSet(false, true)) {
            return;
        }
        try {
            BackupRecord record = backupService.export();
            log.info("定时备份完成：{}", record.getFileName());
            backupService.applyRetention(config.retainCount());
        } catch (Exception e) {
            log.error("定时备份失败", e);
        } finally {
            running.set(false);
        }
    }

    private ScheduleConfig readScheduleConfig() {
        EnvConfig envConfig = envConfigService.getByName(BackupStorageRouter.CONFIG_NAME);
        Map<String, Object> value = envConfig == null ? null : envConfig.getValue();
        Object enabledRaw = value == null ? null : value.get("enabled");
        Object cronRaw = value == null ? null : value.get("cron");
        Object retainRaw = value == null ? null : value.get("retain_count");

        boolean enabled = enabledRaw instanceof Boolean b && b;
        String cron = cronRaw instanceof String s && !s.isBlank() ? s.trim() : "0 0 3 * * ?";
        int retainCount = 0;
        if (retainRaw instanceof Number n) {
            retainCount = n.intValue();
        } else if (retainRaw != null) {
            try {
                retainCount = Integer.parseInt(String.valueOf(retainRaw));
            } catch (NumberFormatException ignored) {
            }
        }
        return new ScheduleConfig(enabled, cron, retainCount);
    }

    private record ScheduleConfig(boolean enabled, String cron, int retainCount) {
    }
}
