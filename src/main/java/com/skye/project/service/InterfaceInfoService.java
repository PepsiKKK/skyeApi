package com.skye.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skye.project.model.entity.InterfaceInfo;


/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add 是否为创建校验
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
