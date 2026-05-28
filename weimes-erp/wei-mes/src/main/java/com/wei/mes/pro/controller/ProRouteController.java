package com.wei.mes.pro.controller;

import com.wei.common.annotation.Log;
import com.wei.common.constant.UserConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.mes.pro.domain.ProRoute;
import com.wei.mes.pro.service.IProRouteProcessService;
import com.wei.mes.pro.service.IProRouteProductBomService;
import com.wei.mes.pro.service.IProRouteProductService;
import com.wei.mes.pro.service.IProRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 工艺路线Controller
 *
 * @author yinjinlu
 * @date 2022-05-12
 */
@RestController
@RequestMapping("/mes/pro/proroute")
public class ProRouteController extends BaseController
{
    @Autowired
    private IProRouteService proRouteService;

    @Autowired
    private IProRouteProcessService proRouteProcessService;

    @Autowired
    private IProRouteProductService proRouteProductService;

    @Autowired
    private IProRouteProductBomService proRouteProductBomService;

    /**
     * 查询工艺路线列表
     * 这是Java代码
     */
    @GetMapping("/list")
    public TableDataInfo list(ProRoute proRoute)
    {
        startPage();
        List<ProRoute> list = proRouteService.selectProRouteList(proRoute);
        return getDataTable(list);
    }

    /**
     * 导出工艺路线列表
     */
    @PreAuthorize("@ss.hasPermi('mes:pro:proroute:export')")
    @Log(title = "工艺路线", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProRoute proRoute)
    {
        List<ProRoute> list = proRouteService.selectProRouteList(proRoute);
        ExcelUtil<ProRoute> util = new ExcelUtil<ProRoute>(ProRoute.class);
        util.exportExcel(response, list, "工艺路线数据");
    }

    /**
     * 获取工艺路线详细信息
     */
    @PreAuthorize("@ss.hasPermi('mes:pro:proroute:query')")
    @GetMapping(value = "/{routeId}")
    public AjaxResult getInfo(@PathVariable("routeId") Long routeId)
    {
        return AjaxResult.success(proRouteService.selectProRouteByRouteId(routeId));
    }

    /**
     * 新增工艺路线
     */
    @PreAuthorize("@ss.hasPermi('mes:pro:proroute:add')")
    @Log(title = "工艺路线", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProRoute proRoute)
    {
        if(UserConstants.NOT_UNIQUE.equals(proRouteService.checkRouteCodeUnique(proRoute))){
            return AjaxResult.error("工艺路线编号已存在！");
        }
        return toAjax(proRouteService.insertProRoute(proRoute));
    }

    /**
     * 修改工艺路线
     */
    @PreAuthorize("@ss.hasPermi('mes:pro:proroute:edit')")
    @Log(title = "工艺路线", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProRoute proRoute)
    {
        if(UserConstants.NOT_UNIQUE.equals(proRouteService.checkRouteCodeUnique(proRoute))){
            return AjaxResult.error("工艺路线编号已存在！");
        }
        return toAjax(proRouteService.updateProRoute(proRoute));
    }

    /**
     * 删除工艺路线
     */
    @PreAuthorize("@ss.hasPermi('mes:pro:proroute:remove')")
    @Log(title = "工艺路线", businessType = BusinessType.DELETE)
    @Transactional
	@DeleteMapping("/{routeIds}")
    public AjaxResult remove(@PathVariable Long[] routeIds)
    {
        for (Long routeId:routeIds
             ) {
            proRouteProcessService.deleteByRouteId(routeId);
            proRouteProductService.deleteByRouteId(routeId);
            proRouteProductBomService.deleteByRouteId(routeId);
        }
        return toAjax(proRouteService.deleteProRouteByRouteIds(routeIds));
    }
}
