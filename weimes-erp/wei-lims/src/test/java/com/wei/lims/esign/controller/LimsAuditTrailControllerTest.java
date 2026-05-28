package com.wei.lims.esign.controller;

import java.util.Collections;

import com.wei.framework.web.exception.GlobalExceptionHandler;
import com.wei.lims.esign.domain.LimsAuditTrail;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 审计追踪Controller单元测试
 */
@ExtendWith(MockitoExtension.class)
class LimsAuditTrailControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILimsAuditTrailService limsAuditTrailService;

    @InjectMocks
    private LimsAuditTrailController controller;

    private LimsAuditTrail buildAuditTrail() {
        LimsAuditTrail trail = new LimsAuditTrail();
        trail.setAuditId(1L);
        trail.setTableName("lims_test_result");
        trail.setRecordId(100L);
        trail.setFieldName("reportedValue");
        trail.setOldValue("99.5");
        trail.setNewValue("99.8");
        trail.setOperationType("UPDATE");
        trail.setOperatorId(1L);
        trail.setOperatorName("张三");
        trail.setReason("修正录入错误");
        return trail;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("查询审计追踪列表 - 正常")
    void list_success() throws Exception {
        when(limsAuditTrailService.selectLimsAuditTrailList(any()))
                .thenReturn(Collections.singletonList(buildAuditTrail()));

        mockMvc.perform(get("/lims/esign/audit/list")
                        .param("tableName", "lims_test_result"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("查询变更历史 - 正常")
    void getHistory_success() throws Exception {
        when(limsAuditTrailService.selectByTableAndRecord("lims_test_result", 100L))
                .thenReturn(Collections.singletonList(buildAuditTrail()));

        mockMvc.perform(get("/lims/esign/audit/history/lims_test_result/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsAuditTrailService).selectByTableAndRecord("lims_test_result", 100L);
    }

    @Test
    @DisplayName("查询变更历史 - Service异常")
    void getHistory_serviceException() throws Exception {
        when(limsAuditTrailService.selectByTableAndRecord(anyString(), anyLong()))
                .thenThrow(new RuntimeException("数据库连接异常"));

        mockMvc.perform(get("/lims/esign/audit/history/lims_test_result/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("查询审计追踪列表 - 空结果")
    void list_emptyResult() throws Exception {
        when(limsAuditTrailService.selectLimsAuditTrailList(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/lims/esign/audit/list"))
                .andExpect(status().isOk());
    }
}
