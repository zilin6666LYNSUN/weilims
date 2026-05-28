package com.wei.lims.sample.controller;

import java.util.Collections;

import com.wei.framework.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.lims.sample.domain.LimsSample;
import com.wei.lims.sample.service.ILimsSampleService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 样品管理Controller单元测试
 */
@ExtendWith(MockitoExtension.class)
class LimsSampleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILimsSampleService limsSampleService;

    @InjectMocks
    private LimsSampleController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LimsSample buildSample() {
        LimsSample sample = new LimsSample();
        sample.setSampleId(1L);
        sample.setSampleCode("SMP-2026-001");
        sample.setSampleName("原料药批次A");
        sample.setSampleType("RAW_MATERIAL");
        sample.setSampleSource("PURCHASE");
        sample.setProductId(10L);
        sample.setProductName("阿莫西林");
        sample.setStatus("REGISTERED");
        return sample;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ==================== CRUD正常流程 ====================

    @Test
    @DisplayName("查询样品列表 - 正常")
    void list_success() throws Exception {
        when(limsSampleService.selectLimsSampleList(any()))
                .thenReturn(Collections.singletonList(buildSample()));

        mockMvc.perform(get("/lims/sample/list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("查询样品详情 - 正常")
    void getInfo_success() throws Exception {
        when(limsSampleService.selectLimsSampleBySampleId(1L)).thenReturn(buildSample());

        mockMvc.perform(get("/lims/sample/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增样品 - 正常")
    void add_success() throws Exception {
        LimsSample sample = buildSample();
        when(limsSampleService.checkSampleCodeUnique(any())).thenReturn("0");
        when(limsSampleService.insertLimsSample(any())).thenReturn(1);

        mockMvc.perform(post("/lims/sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增样品 - 编号重复")
    void add_duplicateCode() throws Exception {
        LimsSample sample = buildSample();
        when(limsSampleService.checkSampleCodeUnique(any())).thenReturn("1");

        mockMvc.perform(post("/lims/sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        verify(limsSampleService, never()).insertLimsSample(any());
    }

    @Test
    @DisplayName("修改样品 - 正常")
    void edit_success() throws Exception {
        LimsSample sample = buildSample();
        when(limsSampleService.checkSampleCodeUnique(any())).thenReturn("0");
        when(limsSampleService.updateLimsSample(any())).thenReturn(1);

        mockMvc.perform(put("/lims/sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除样品 - 正常")
    void remove_success() throws Exception {
        when(limsSampleService.deleteLimsSampleBySampleIds(any(Long[].class))).thenReturn(1);

        mockMvc.perform(delete("/lims/sample/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 业务流程 ====================

    @Test
    @DisplayName("接收样品 - 正常")
    void receive_success() throws Exception {
        when(limsSampleService.receiveSample(1L)).thenReturn(1);

        mockMvc.perform(put("/lims/sample/receive/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsSampleService).receiveSample(1L);
    }

    @Test
    @DisplayName("样品状态变更 - 正常")
    void changeStatus_success() throws Exception {
        when(limsSampleService.changeSampleStatus(1L, "TESTING", "开始检验")).thenReturn(1);

        mockMvc.perform(put("/lims/sample/status/1/TESTING")
                        .param("reason", "开始检验"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 异常场景 ====================

    @Test
    @DisplayName("接收样品 - 状态不允许")
    void receive_invalidStatus() throws Exception {
        when(limsSampleService.receiveSample(1L)).thenReturn(0);

        mockMvc.perform(put("/lims/sample/receive/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("接收样品 - Service抛异常")
    void receive_serviceException() throws Exception {
        when(limsSampleService.receiveSample(1L))
                .thenThrow(new RuntimeException("样品不存在"));

        mockMvc.perform(put("/lims/sample/receive/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("删除样品 - 记录不存在")
    void remove_notFound() throws Exception {
        when(limsSampleService.deleteLimsSampleBySampleIds(any(Long[].class))).thenReturn(0);

        mockMvc.perform(delete("/lims/sample/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
