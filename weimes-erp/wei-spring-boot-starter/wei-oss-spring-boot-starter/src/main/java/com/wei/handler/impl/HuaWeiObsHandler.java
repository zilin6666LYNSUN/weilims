package com.wei.handler.impl;

import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectRequest;
import com.wei.config.HuaWeiObsConfig;
import com.wei.config.OssProperties;
import com.wei.exception.weiOssException;
import com.wei.handler.IOSSHandler;
import com.wei.utils.CommonUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @ClassName HuaWeiObsHandler
 * @Description:
 * @Author: 施子安
 * @CreateDate: 2025/2/8 19:57
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/2/8 19:57
 * @UpdateRemark:
 * @Version: 1.0
 */
@Slf4j
@Service("huaWeiObsService")
@Import(HuaWeiObsConfig.class)
public class HuaWeiObsHandler implements IOSSHandler {

    @Resource
    private OssProperties ossProperties;

    @Autowired(required = false)
    private ObsClient obsClient;

    /**
     * 上传文件
     *
     * @param fileName    文件名
     * @param inputStream 文件
     * @return obs访问路径
     */
    @Override
    public String uploadFile(String fileName, InputStream inputStream) {
        return this.uploadFileWithPrefix("", fileName, inputStream);
    }

    /**
     * 上传文件，保存后带obs前缀路径
     *
     * @param prefix      前缀文件夹
     * @param fileName    文件名
     * @param inputStream 文件
     * @return obs访问路径
     */
    @Override
    public String uploadFileWithPrefix(String prefix, String fileName, InputStream inputStream) {
        String filePath = CommonUtils.buildFilePath(prefix, fileName);
        try {
            obsClient.putObject(new PutObjectRequest(ossProperties.getBucket(), filePath, inputStream));
        } catch (Exception e) {
            throw new weiOssException("huaWeiObs，请确认是否已经在配置文件中正确配置了huaWeiObs", e);
        }
        // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
        return "https://" +
                ossProperties.getBucket() +
                "." +
                ossProperties.getEndPoint() +
                CommonUtils.separator +
                filePath;
    }

    /**
     * 删除文件
     *
     * @param url
     */
    @Override
    public void deleteFile(String url) {
        if (StringUtils.isBlank(url)) {
            throw new weiOssException("文件不能为空!");
        }
        try {
            String objectName = CommonUtils.buildUrlPath(url, ossProperties.getBucket(), ossProperties.getEndPoint());
            obsClient.deleteObject(ossProperties.getBucket(), objectName);
        } catch (Exception e) {
            throw new weiOssException("huaWeiObs删除文件失败，请确认是否已经在配置文件中正确配置了huaWeiObs", e);
        }
    }

    @Override
    public void downLoadFile(String url, HttpServletResponse response) {
        if (StringUtils.isBlank(url)) {
            throw new weiOssException("文件不能为空!");
        }
        try {
            String objectName = CommonUtils.buildUrlPath(url, ossProperties.getBucket(), ossProperties.getEndPoint());
            // 判断文件是否存在
            boolean doesObjectExist = obsClient.doesObjectExist(ossProperties.getBucket(), objectName);
            if (!doesObjectExist) {
                throw new weiOssException("文件名称不存在!");
            }
            // 流式下载
            ObsObject obsObject = obsClient.getObject(ossProperties.getBucket(), objectName);
            InputStream input = obsObject.getObjectContent();
            byte[] b = new byte[1024];
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(objectName, "UTF-8"));
            OutputStream outputStream = response.getOutputStream();
            int len;
            while ((len = input.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            outputStream.close();
            input.close();
        } catch (Exception e) {
            throw new weiOssException("huaWeiObs下载文件失败，请确认是否已经在配置文件中正确配置了huaWeiObs", e);
        }
    }
}
