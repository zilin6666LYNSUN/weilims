package com.wei.lims.test.controller;

import java.util.Collections;

import com.wei.framework.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.lims.test.domain.LimsTestOrder;
import com.wei.lims.test.service.ILimsTestOrderService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 检验订单Controller单元测试
 */
@ExtendWith(MockitoExtension.class)
class LimsTestOrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILimsTestOrderService limsTestOrderService;

    @Mock
    private ILimsTestResultService limsTestResultService;

    @InjectMocks
    private LimsTestOrderController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LimsTestOrder buildTestOrder() {
        LimsTestOrder order = new LimsTestOrder();
        order.setTestOrderId(1L);
        order.setTestOrderCode("TO-2026-001");
        order.setTestOrderName("原料药检验");
        order.setSampleId(100L);
        order.setSpecificationId(200L);
        order.setStatus("PENDING");
        order.setPriority("NORMAL");
        order.setTestType("ROUTINE");
        order.setTesterId(1L);
        return order;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ==================== CRUD正常流程 ====================

    @Test
    @DisplayName("查询检验订单列表 - 正常")
    void list_success() throws Exception {
        when(limsTestOrderService.selectLimsTestOrderList(any()))
                .thenReturn(Collections.singletonList(buildTestOrder()));

        mockMvc.perform(get("/lims/test/order/list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("查询检验订单详情 - 正常")
    void getInfo_success() throws Exception {
        when(limsTestOrderService.selectLimsTestOrderByTestOrderId(1L)).thenReturn(buildTestOrder());

        mockMvc.perform(get("/lims/test/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增检验订单 - 正常")
    void add_success() throws Exception {
        LimsTestOrder order = buildTestOrder();
        when(limsTestOrderService.checkTestOrderCodeUnique(any())).thenReturn("0");
        when(limsTestOrderService.insertLimsTestOrder(any())).thenReturn(1);

        mockMvc.perform(post("/lims/test/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增检验订单 - 编号重复")
    void add_duplicateCode() throws Exception {
        LimsTestOrder order = buildTestOrder();
        when(limsTestOrderService.checkTestOrderCodeUnique(any())).thenReturn("1");

        mockMvc.perform(post("/lims/test/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        verify(limsTestOrderService, never()).insertLimsTestOrder(any());
    }

    @Test
    @DisplayName("修改检验订单 - 正常")
    void edit_success() throws Exception {
        LimsTestOrder order = buildTestOrder();
        when(limsTestOrderService.checkTestOrderCodeUnique(any())).thenReturn("0");
        when(limsTestOrderService.updateLimsTestOrder(any())).thenReturn(1);

        mockMvc.perform(put("/lims/test/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除检验订单 - 正常")
    void remove_success() throws Exception {
        when(limsTestOrderService.deleteLimsTestOrderByTestOrderIds(any(Long[].class))).thenReturn(1);

        mockMvc.perform(delete("/lims/test/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 业务流程 ====================

    @Test
    @DisplayName("提交检验订单 - 正常")
    void submit_success() throws Exception {
        when(limsTestOrderService.submitTestOrder(1L)).thenReturn(1);

        mockMvc.perform(put("/lims/test/order/submit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsTestOrderService).submitTestOrder(1L);
    }

    @Test
    @DisplayName("检验复核 - 通过")
    void review_pass() throws Exception {
        when(limsTestOrderService.reviewTestOrder(1L, "PASS", "复核通过")).thenReturn(1);

        mockMvc.perform(put("/lims/test/order/review/1")
                        .param("reviewResult", "PASS")
                        .param("comment", "复核通过"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("检验批准 - 通过")
    void approve_pass() throws Exception {
        when(limsTestOrderService.approveTestOrder(1L, "APPROVED", "批准通过")).thenReturn(1);

        mockMvc.perform(put("/lims/test/order/approve/1")
                        .param("approveResult", "APPROVED")
                        .param("comment", "批准通过"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 异常场景 ====================

    @Test
    @DisplayName("提交检验订单 - 状态不允许")
    void submit_invalidStatus() throws Exception {
        when(limsTestOrderService.submitTestOrder(1L)).thenReturn(0);

        mockMvc.perform(put("/lims/test/order/submit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("检验复核 - Service抛异常")
    void review_serviceException() throws Exception {
        when(limsTestOrderService.reviewTestOrder(1L, "PASS", "comment"))
                .thenThrow(new RuntimeException("当前状态不允许复核"));

        mockMvc.perform(put("/lims/test/order/review/1")
                        .param("reviewResult", "PASS")
                        .param("comment", "comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("删除检验订单 - 记录不存在")
    void remove_notFound() throws Exception {
        when(limsTestOrderService.deleteLimsTestOrderByTestOrderIds(any(Long[].class))).thenReturn(0);

        mockMvc.perform(delete("/lims/test/order/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("查询检验结果列表 - 正常")
    void resultList_success() throws Exception {
        when(limsTestResultService.selectLimsTestResultByTestOrderId(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/lims/test/order/result/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
