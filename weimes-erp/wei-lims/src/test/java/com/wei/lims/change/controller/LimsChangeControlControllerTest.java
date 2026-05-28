package com.wei.lims.change.controller;

import java.util.Collections;

import com.wei.framework.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.lims.change.domain.LimsChangeControl;
import com.wei.lims.change.service.ILimsChangeControlService;
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
 * 变更控制Controller单元测试
 */
@ExtendWith(MockitoExtension.class)
class LimsChangeControlControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILimsChangeControlService limsChangeControlService;

    @InjectMocks
    private LimsChangeControlController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LimsChangeControl buildChangeControl() {
        LimsChangeControl cc = new LimsChangeControl();
        cc.setChangeId(1L);
        cc.setChangeCode("CC-2026-001");
        cc.setChangeTitle("检验方法变更");
        cc.setChangeType("PERMANENT");
        cc.setChangeCategory("METHOD");
        cc.setChangeReason("提升检测精度");
        cc.setStatus("DRAFT");
        cc.setRequesterId(1L);
        return cc;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ==================== CRUD正常流程 ====================

    @Test
    @DisplayName("查询变更列表 - 正常")
    void list_success() throws Exception {
        when(limsChangeControlService.selectLimsChangeControlList(any()))
                .thenReturn(Collections.singletonList(buildChangeControl()));

        mockMvc.perform(get("/lims/change/list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("查询变更详情 - 正常")
    void getInfo_success() throws Exception {
        when(limsChangeControlService.selectByChangeId(1L)).thenReturn(buildChangeControl());

        mockMvc.perform(get("/lims/change/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增变更 - 正常")
    void add_success() throws Exception {
        LimsChangeControl cc = buildChangeControl();
        when(limsChangeControlService.insertLimsChangeControl(any())).thenReturn(1);

        mockMvc.perform(post("/lims/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改变更 - 正常")
    void edit_success() throws Exception {
        LimsChangeControl cc = buildChangeControl();
        when(limsChangeControlService.updateLimsChangeControl(any())).thenReturn(1);

        mockMvc.perform(put("/lims/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除变更 - 正常")
    void remove_success() throws Exception {
        when(limsChangeControlService.deleteLimsChangeControlByIds(any(Long[].class))).thenReturn(1);

        mockMvc.perform(delete("/lims/change/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 工作流正常流程 ====================

    @Test
    @DisplayName("提交变更 - 正常")
    void submit_success() throws Exception {
        when(limsChangeControlService.submitChange(1L)).thenReturn(1);

        mockMvc.perform(put("/lims/change/submit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(limsChangeControlService).submitChange(1L);
    }

    @Test
    @DisplayName("批准变更 - 正常")
    void approve_success() throws Exception {
        when(limsChangeControlService.approveChange(1L)).thenReturn(1);

        mockMvc.perform(put("/lims/change/approve/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("实施变更 - 正常")
    void implement_success() throws Exception {
        when(limsChangeControlService.implementChange(1L)).thenReturn(1);

        mockMvc.perform(put("/lims/change/implement/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("完成变更 - 正常")
    void complete_success() throws Exception {
        when(limsChangeControlService.completeChange(1L)).thenReturn(1);

        mockMvc.perform(put("/lims/change/complete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 业务异常场景 ====================

    @Test
    @DisplayName("提交变更 - 状态不允许提交")
    void submit_invalidStatus() throws Exception {
        when(limsChangeControlService.submitChange(1L)).thenReturn(0);

        mockMvc.perform(put("/lims/change/submit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("批准变更 - Service抛异常(状态不正确)")
    void approve_invalidStatus() throws Exception {
        when(limsChangeControlService.approveChange(1L))
                .thenThrow(new RuntimeException("当前状态不允许批准操作"));

        mockMvc.perform(put("/lims/change/approve/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("实施变更 - Service抛异常")
    void implement_serviceException() throws Exception {
        when(limsChangeControlService.implementChange(1L))
                .thenThrow(new RuntimeException("实施计划未填写"));

        mockMvc.perform(put("/lims/change/implement/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("删除变更 - 记录不存在")
    void remove_notFound() throws Exception {
        when(limsChangeControlService.deleteLimsChangeControlByIds(any(Long[].class))).thenReturn(0);

        mockMvc.perform(delete("/lims/change/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
