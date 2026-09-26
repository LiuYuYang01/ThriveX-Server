package liuyuyang.net.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import liuyuyang.net.core.execption.CustomException;
import liuyuyang.net.core.utils.Result;
import liuyuyang.net.web.service.impl.StatisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Tag(name = "数据统计管理")
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
    @Operation(summary = "获取网站统计数据")
    public Result<JsonNode> getStatisData(
            @Parameter(description = "统计类型：basic(基础数据), overview(概览趋势), new-visitor(新访客趋势), basic-overview(基础概览趋势), region(地域分布), source(来源分布), client(设备分布)", required = true) @RequestParam String type,
            @Parameter(description = "开始日期，格式: 20240101，可选，默认为当天") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期，格式: 20240131，可选，默认为当天") @RequestParam(required = false) String endDate
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
                    data = baiduService.getBasicOverviewTrend(startDate, endDate);
                    successMsg = "获取基础概览时间趋势报表成功";
                    break;
                case "region":
                    data = baiduService.getRegionReport(startDate, endDate);
                    successMsg = "获取地域分布报表成功";
                    break;
                case "source":
                    data = baiduService.getSourceReport(startDate, endDate);
                    successMsg = "获取来源分布报表成功";
                    break;
                case "client":
                    data = baiduService.getClientReport(startDate, endDate);
                    successMsg = "获取设备分布报表成功";
                    break;
                default:
                    return Result.error("不支持的统计类型: " + type
                            + "。支持的类型: basic, overview, new-visitor, basic-overview, region, source, client");
            }

            if (data == null) {
                return Result.error(600, "获取" + type + "类型统计数据失败");
            }

            return Result.success(successMsg, data);

        } catch (CustomException e) {
            // 业务提示（如 token 失效）不含内部细节，可直接返回
            return Result.error(600, e.getMessage());
        } catch (Exception e) {
            // 内部异常细节只进日志，不回显给客户端
            log.error("获取{}类型统计数据失败", type, e);
            return Result.error(600, "获取" + type + "类型统计数据失败");
        }
    }
}
