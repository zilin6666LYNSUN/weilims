package com.wei.print.mapper;

import com.wei.print.domain.PrintClient;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrintClientMapper {
    List<PrintClient> getClientList(PrintClient client);

    int insertClient(PrintClient client);

    PrintClient checkCilentCodeUnique(PrintClient client);

    PrintClient selectById(Long clientId);

    int updateClient(PrintClient client);

    int deleteByIds(@Param("clientIds") List<String> clientIds);
}
