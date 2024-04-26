package com.skye.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.skye.common.model.entity.InterfaceInfo;
import com.skye.project.common.DeleteRequest;
import com.skye.project.common.IdRequest;
import com.skye.project.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.skye.project.model.dto.interfaceInfo.InterfaceInfoInvokeRequest;
import com.skye.project.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.skye.project.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


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

    /**
     * 添加接口
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request);

    /**
     * 删除接口
     * @param deleteRequest
     * @param request
     * @return
     */
    boolean deleteInterfaceInfo(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新接口
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request);

    /**
     * 获取接口列表
     * @param interfaceInfoQueryRequest
     * @return
     */
    List<InterfaceInfo> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 获取分页列表
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    Page<InterfaceInfo> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request);

    /**
     * 上线接口
     * @param idRequest
     * @param request
     * @return
     */
    boolean onlineInterfaceInfo(IdRequest idRequest, HttpServletRequest request);

    /**
     * 下线接口
     * @param idRequest
     * @param request
     * @return
     */
    boolean offlineInterfaceInfo(IdRequest idRequest, HttpServletRequest request);

    /**
     * 调用接口
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    String invokeInterfaceInfo(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request);

}
