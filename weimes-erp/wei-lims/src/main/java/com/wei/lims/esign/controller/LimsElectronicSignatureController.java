package com.wei.lims.esign.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.esign.domain.LimsElectronicSignature;
import com.wei.lims.esign.service.ILimsElectronicSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 电子签名Controller
 */
@RestController
@RequestMapping("/lims/esign")
public class LimsElectronicSignatureController extends BaseController {

    @Autowired
    private ILimsElectronicSignatureService limsElectronicSignatureService;

    /**
     * 创建电子签名
     */
    @PreAuthorize("@ss.hasPermi('lims:esign:add')")
    @Log(title = "电子签名", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult sign(@RequestBody LimsElectronicSignature signature) {
        Long signatureId = limsElectronicSignatureService.createSignature(
                signature.getRecordType(),
                signature.getRecordId(),
                signature.getSignatureMeaning(),
                signature.getReason()
        );
        return AjaxResult.success(signatureId);
    }

    /**
     * 验证电子签名
     */
    @PreAuthorize("@ss.hasPermi('lims:esign:verify')")
    @PostMapping("/verify/{signatureId}")
    public AjaxResult verify(@PathVariable Long signatureId, @RequestParam String password) {
        boolean valid = limsElectronicSignatureService.verifySignature(signatureId, password);
        return valid ? AjaxResult.success("签名验证通过") : AjaxResult.error("签名验证失败");
    }

    /**
     * 查询签名列表
     */
    @PreAuthorize("@ss.hasPermi('lims:esign:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsElectronicSignature signature) {
        startPage();
        List<LimsElectronicSignature> list = limsElectronicSignatureService.selectLimsElectronicSignatureList(signature);
        return getDataTable(list);
    }

    /**
     * 查询指定业务记录的签名
     */
    @PreAuthorize("@ss.hasPermi('lims:esign:query')")
    @GetMapping("/records/{recordType}/{recordId}")
    public AjaxResult getSignatures(@PathVariable String recordType, @PathVariable Long recordId) {
        return AjaxResult.success(limsElectronicSignatureService.selectByRecordTypeAndId(recordType, recordId));
    }
}
