package liuyuyang.net.web.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface StatisService {
    /**
     * 获取百度统计数据
     * @param startDate 开始日期 (格式: 20240101)，可选
     * @param endDate 结束日期 (格式: 20240131)，可选
     * @return 统计数据的JsonNode对象
     */
    JsonNode getStatisData(String startDate, String endDate);
    
    /**
     * 获取概览时间趋势报表（包含PV、IP、跳出率、平均访问时长）
     * @param startDate 开始日期 (格式: 20240101)，可选
     * @param endDate 结束日期 (格式: 20240131)，可选
     * @return 概览统计数据的JsonNode对象
     */
    JsonNode getOverviewTimeTrend(String startDate, String endDate);
    
    /**
     * 获取新访客趋势报表
     * @param startDate 开始日期 (格式: 20240101)，可选
     * @param endDate 结束日期 (格式: 20240131)，可选
     * @return 新访客统计数据的JsonNode对象
     */
    JsonNode getNewVisitorTrend(String startDate, String endDate);
    
    /**
     * 获取基础概览时间趋势报表（只包含PV和IP）
     * @param startDate 开始日期 (格式: 20240101)，可选
     * @param endDate 结束日期 (格式: 20240131)，可选
     * @return 基础统计数据的JsonNode对象
     */
    JsonNode getBasicOverviewTrend(String startDate, String endDate);

    /**
     * 获取地域分布报表（省份维度）
     * @param startDate 开始日期 (格式: 20240101)，可选
     * @param endDate 结束日期 (格式: 20240131)，可选
     * @return 地域分布统计数据的JsonNode对象
     */
    JsonNode getRegionReport(String startDate, String endDate);

    /**
     * 获取来源类型报表（直接访问/外部链接/搜索引擎）
     * @param startDate 开始日期 (格式: 20240101)，可选
     * @param endDate 结束日期 (格式: 20240131)，可选
     * @return 来源分布统计数据的JsonNode对象
     */
    JsonNode getSourceReport(String startDate, String endDate);

    /**
     * 获取设备/客户端分布报表（PC/移动端等）
     * @param startDate 开始日期 (格式: 20240101)，可选
     * @param endDate 结束日期 (格式: 20240131)，可选
     * @return 设备分布统计数据的JsonNode对象
     */
    JsonNode getClientReport(String startDate, String endDate);
}