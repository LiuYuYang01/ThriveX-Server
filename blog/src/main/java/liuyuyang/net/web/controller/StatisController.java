package liuyuyang.net.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.impl.StatisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "数据统计管理")
@Slf4j
@RestController
@RequestMapping("/statis")
public class StatisController {
    @Resource
    private StatisServiceImpl baiduService;

    /**
     * 统一的百度统计数据获取接口
     *
     * @param type      统计类型：basic(基础数据), overview(概览趋势), new-visitor(新访客趋势), basic-overview(基础概览趋势)
     * @param startDate 开始日期 (格式: 20240101)，可选，默认为当天
     * @param endDate   结束日期 (格式: 20240131)，可选，默认为当天
     */
    @GetMapping
    @ApiOperation("获取网站统计数据")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<JsonNode> getStatisData(
            @ApiParam(value = "统计类型：basic(基础数据), overview(概览趋势), new-visitor(新访客趋势), basic-overview(基础概览趋势)", required = true) @RequestParam String type,
            @ApiParam(value = "开始日期，格式: 20240101，可选，默认为当天") @RequestParam(required = false) String startDate,
            @ApiParam(value = "结束日期，格式: 20240131，可选，默认为当天") @RequestParam(required = false) String endDate
    ) {
        try {
            JsonNode data = null;
            String successMsg = "";

            switch (type.toLowerCase()) {
                case "basic":
                    data = baiduService.getStatisData(startDate, endDate);
                    successMsg = "获取基础统计数据成功";
                    break;
                case "overview":
                    data = baiduService.getOverviewTimeTrend(startDate, endDate);
                    successMsg = "获取概览时间趋势报表成功";
                    break;
                case "new-visitor":
                    data = baiduService.getNewVisitorTrend(startDate, endDate);
                    successMsg = "获取新访客趋势报表成功";
                    break;
                case "basic-overview":
                    System.out.println("basic-overview");
                    System.out.println("startDate: " + startDate);
                    System.out.println("endDate: " + endDate);
                    data = baiduService.getBasicOverviewTrend(startDate, endDate);
                    successMsg = "获取基础概览时间趋势报表成功";
                    break;
                default:
                    return Result.error("不支持的统计类型: " + type + "。支持的类型: basic, overview, new-visitor, basic-overview");
            }

            if (data == null) {
                return Result.error(600, "获取" + type + "类型统计数据失败");
            }

            return Result.success(successMsg, data);

        } catch (Exception e) {
            log.error("获取{}类型统计数据失败", type, e);
            return Result.error(600, "获取" + type + "类型统计数据失败: " + e.getMessage());
        }
    }
}
