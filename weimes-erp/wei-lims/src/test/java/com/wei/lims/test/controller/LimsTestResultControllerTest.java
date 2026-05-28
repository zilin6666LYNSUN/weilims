package com.wei.lims.test.controller;

import java.math.BigDecimal;
import java.util.Arrays;

import com.wei.framework.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.common.core.domain.AjaxResult;
import com.wei.lims.test.domain.LimsTestResult;
import com.wei.lims.test.service.ILimsTestResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 检验结果Controller单元测试
 */
@ExtendWith(MockitoExtension.class)
class LimsTestResultControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILimsTestResultService limsTestResultService;

    @InjectMocks
    private LimsTestResultController limsTestResultController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LimsTestResult buildTestResult() {
        LimsTestResult result = new LimsTestResult();
        result.setResultId(1L);
        result.setTestOrderId(100L);
        result.setSpecLineId(200L);
        result.setTestItemName("含量测定");
        result.setTestItemType("QUANTITATIVE");
        result.setOriginalValue("99.5");
        result.setReportedValue("99.5");
        result.setLowerLimit(new BigDecimal("95.0"));
        result.setUpperLimit(new BigDecimal("105.0"));
        result.setResultConclusion("PASS");
        result.setIsOos(Integer.valueOf(0));
        result.setIsOot(Integer.valueOf(0));
        return result;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(limsTestResultController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ==================== 正常流程 ====================

    @Test
    @DisplayName("录入检验结果 - 正常")
    void addResult_success() throws Exception {
        LimsTestResult result = buildTestResult();
        when(limsTestResultService.insertLimsTestResult(any(LimsTestResult.class))).thenReturn(1);

        mockMvc.perform(post("/lims/test/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsTestResultService, times(1)).insertLimsTestResult(any(LimsTestResult.class));
    }

    @Test
    @DisplayName("批量录入检验结果 - 正常")
    void batchAddResult_success() throws Exception {
        LimsTestResult r1 = buildTestResult();
        LimsTestResult r2 = buildTestResult();
        r2.setTestItemName("溶出度");
        when(limsTestResultService.insertLimsTestResult(any(LimsTestResult.class))).thenReturn(1);

        mockMvc.perform(post("/lims/test/result/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(r1, r2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsTestResultService, times(2)).insertLimsTestResult(any(LimsTestResult.class));
    }

    @Test
    @DisplayName("修改检验结果 - 正常")
    void editResult_success() throws Exception {
        LimsTestResult result = buildTestResult();
        result.setReportedValue("99.8");
        when(limsTestResultService.updateLimsTestResult(any(LimsTestResult.class))).thenReturn(1);

        mockMvc.perform(put("/lims/test/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsTestResultService, times(1)).updateLimsTestResult(any(LimsTestResult.class));
    }

    @Test
    @DisplayName("删除检验结果 - 正常")
    void removeResult_success() throws Exception {
        when(limsTestResultService.deleteLimsTestResultByResultIds(any(Long[].class))).thenReturn(2);

        mockMvc.perform(delete("/lims/test/result/1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsTestResultService, times(1)).deleteLimsTestResultByResultIds(any(Long[].class));
    }

    // ==================== 异常场景 ====================

    @Test
    @DisplayName("修改检验结果 - Service返回0(记录不存在)")
    void editResult_notFound() throws Exception {
        LimsTestResult result = buildTestResult();
        result.setResultId(999L);
        when(limsTestResultService.updateLimsTestResult(any(LimsTestResult.class))).thenReturn(0);

        mockMvc.perform(put("/lims/test/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("删除检验结果 - Service返回0(记录不存在)")
    void removeResult_notFound() throws Exception {
        when(limsTestResultService.deleteLimsTestResultByResultIds(any(Long[].class))).thenReturn(0);

        mockMvc.perform(delete("/lims/test/result/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("批量录入检验结果 - 空列表")
    void batchAddResult_emptyList() throws Exception {
        mockMvc.perform(post("/lims/test/result/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());

        verify(limsTestResultService, never()).insertLimsTestResult(any());
    }

    @Test
    @DisplayName("OOS结果录入 - isOos=1标记超限")
    void addResult_oosFlag() throws Exception {
        LimsTestResult result = buildTestResult();
        result.setReportedValue("85.0");
        result.setResultConclusion("FAIL");
        result.setIsOos(Integer.valueOf(1));
        when(limsTestResultService.insertLimsTestResult(any(LimsTestResult.class))).thenReturn(1);

        mockMvc.perform(post("/lims/test/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsTestResultService).insertLimsTestResult(argThat(r ->
                r.getIsOos() != null && r.getIsOos() == 1 && "FAIL".equals(r.getResultConclusion())
        ));
    }
}
