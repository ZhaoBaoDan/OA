package com.smartauto.oa.report.service;

import java.util.Map;

/**
 * 报表服务接口
 */
public interface ReportService {

    Map<String, Object> getOverview();

    Map<String, Object> getAttendanceReport(int year, int month);

    Map<String, Object> getTaskReport(Long userId);

    Map<String, Object> getAssetReport();
}
