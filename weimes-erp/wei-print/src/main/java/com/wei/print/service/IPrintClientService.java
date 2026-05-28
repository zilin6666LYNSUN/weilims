package com.wei.print.service;

import com.wei.common.core.domain.AjaxResult;
import com.wei.print.domain.PrintClient;

import java.util.List;

public interface IPrintClientService {
    List<PrintClient> getClientList(PrintClient client);

    AjaxResult insertClient(PrintClient client);

    AjaxResult selectClientById(Long clientId);

    AjaxResult updateClient(PrintClient client);

    AjaxResult remove(String clientIds);

    AjaxResult getWorkshopAndWorkstation();

    AjaxResult getAll();
}
