package liuyuyang.net.core.service;

import liuyuyang.net.vo.file.FileCompressItemVO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存记录 pfop 任务上下文，供轮询完成后比对体积并覆盖原 key。
 */
@Component
public class CompressTaskStore {

    public static final class Context {
        private final String path;
        private final String name;
        private final String key;
        private final String tmpKey;
        private final long beforeSize;

        public Context(String path, String name, String key, String tmpKey, long beforeSize) {
            this.path = path;
            this.name = name;
            this.key = key;
            this.tmpKey = tmpKey;
            this.beforeSize = beforeSize;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }

        public String getTmpKey() {
            return tmpKey;
        }

        public long getBeforeSize() {
            return beforeSize;
        }
    }

    private final Map<String, Context> tasks = new ConcurrentHashMap<>();

    public void put(String persistentId, Context context) {
        tasks.put(persistentId, context);
    }

    public Context get(String persistentId) {
        return tasks.get(persistentId);
    }

    public void remove(String persistentId) {
        tasks.remove(persistentId);
    }

    public FileCompressItemVO toProcessingItem(Context context, String persistentId) {
        FileCompressItemVO item = new FileCompressItemVO();
        item.setPath(context.getPath());
        item.setName(context.getName());
        item.setStatus("processing");
        item.setTaskId(persistentId);
        item.setBeforeSize(context.getBeforeSize());
        item.setMessage("七牛持久化处理中…");
        return item;
    }
}
