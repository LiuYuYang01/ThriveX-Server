package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Record;
import liuyuyang.net.dto.FilterDTO;
import liuyuyang.net.dto.PageDTO;

import java.util.List;

public interface RecordService extends IService<Record> {
    List<Record> list(FilterDTO filterDTO);
    Page<Record> paging(FilterDTO filterDTO, PageDTO pageDTO);
}
