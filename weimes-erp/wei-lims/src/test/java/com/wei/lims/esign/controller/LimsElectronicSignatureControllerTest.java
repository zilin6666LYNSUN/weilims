package com.wei.lims.esign.controller;

import java.util.Collections;
import java.util.Date;

import com.wei.framework.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.lims.esign.domain.LimsElectronicSignature;
import com.wei.lims.esign.service.ILimsElectronicSignatureService;
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
 * 电子签名Controller单元测试
 */
@ExtendWith(MockitoExtension.class)
class LimsElectronicSignatureControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILimsElectronicSignatureService limsElectronicSignatureService;

    @InjectMocks
    private LimsElectronicSignatureController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LimsElectronicSignature buildSignature() {
        LimsElectronicSignature sig = new LimsElectronicSignature();
        sig.setSignatureId(1L);
        sig.setRecordType("LimsTestOrder");
        sig.setRecordId(100L);
        sig.setSignerId(1L);
        sig.setSignerName("张三");
        sig.setSignatureMeaning("APPROVED");
        sig.setSignatureTime(new Date());
        sig.setReason("检验结果审核通过");
        sig.setIsValid(1);
        sig.setIpAddress("192.168.1.100");
        return sig;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("创建电子签名 - APPROVED含义")
    void sign_approved_success() throws Exception {
        LimsElectronicSignature sig = buildSignature();
        when(limsElectronicSignatureService.createSignature("LimsTestOrder", 100L, "APPROVED", "检验结果审核通过"))
                .thenReturn(1L);

        mockMvc.perform(post("/lims/esign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));

        verify(limsElectronicSignatureService).createSignature("LimsTestOrder", 100L, "APPROVED", "检验结果审核通过");
    }

    @Test
    @DisplayName("创建电子签名 - REVIEWED含义")
    void sign_reviewed_success() throws Exception {
        LimsElectronicSignature sig = buildSignature();
        sig.setSignatureMeaning("REVIEWED");
        sig.setReason("复核通过");
        when(limsElectronicSignatureService.createSignature(anyString(), anyLong(), eq("REVIEWED"), anyString()))
                .thenReturn(2L);

        mockMvc.perform(post("/lims/esign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("创建电子签名 - REJECTED含义")
    void sign_rejected_success() throws Exception {
        LimsElectronicSignature sig = buildSignature();
        sig.setSignatureMeaning("REJECTED");
        sig.setReason("检验结果不符合要求");
        when(limsElectronicSignatureService.createSignature(anyString(), anyLong(), eq("REJECTED"), anyString()))
                .thenReturn(3L);

        mockMvc.perform(post("/lims/esign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("验证电子签名 - 通过")
    void verify_success() throws Exception {
        when(limsElectronicSignatureService.verifySignature(1L, "correctPassword")).thenReturn(true);

        mockMvc.perform(post("/lims/esign/verify/1")
                        .param("password", "correctPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("签名验证通过"));
    }

    @Test
    @DisplayName("验证电子签名 - 失败(密码错误)")
    void verify_wrongPassword() throws Exception {
        when(limsElectronicSignatureService.verifySignature(1L, "wrongPassword")).thenReturn(false);

        mockMvc.perform(post("/lims/esign/verify/1")
                        .param("password", "wrongPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("签名验证失败"));
    }

    @Test
    @DisplayName("验证电子签名 - 签名不存在")
    void verify_notFound() throws Exception {
        when(limsElectronicSignatureService.verifySignature(999L, "anyPassword"))
                .thenThrow(new RuntimeException("签名记录不存在"));

        mockMvc.perform(post("/lims/esign/verify/999")
                        .param("password", "anyPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("查询签名列表 - 正常")
    void list_success() throws Exception {
        when(limsElectronicSignatureService.selectLimsElectronicSignatureList(any()))
                .thenReturn(Collections.singletonList(buildSignature()));

        mockMvc.perform(get("/lims/esign/list")
                        .param("recordType", "LimsTestOrder"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("查询指定业务记录的签名 - 正常")
    void getSignatures_success() throws Exception {
        when(limsElectronicSignatureService.selectByRecordTypeAndId("LimsTestOrder", 100L))
                .thenReturn(Collections.singletonList(buildSignature()));

        mockMvc.perform(get("/lims/esign/records/LimsTestOrder/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("创建电子签名 - Service抛异常(重复签名)")
    void sign_duplicateException() throws Exception {
        LimsElectronicSignature sig = buildSignature();
        when(limsElectronicSignatureService.createSignature(anyString(), anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("该用户已对此记录签名"));

        mockMvc.perform(post("/lims/esign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
