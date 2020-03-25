package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.forbes.comm.exception.ForbesException;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.comm.PayBizResultEnum;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.mapper.MchInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class MchInfoServiceImpl extends ServiceImpl<MchInfoMapper, MchInfo> implements IMchInfoService {


    /***商家提点数
     * @param entity
     * @return
     */
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean save(MchInfo entity) {
        if(entity.getReflectPoints().compareTo(BigDecimal.ZERO) < 0){
            throw new ForbesException(PayBizResultEnum.MCH_POINTS.getBizCode(),PayBizResultEnum.MCH_POINTS.getBizMessage());
        }
        boolean isAdd =  SqlHelper.retBool(baseMapper.insert(entity));
        return isAdd;
    }


    /***商家提点数
     * @param entity
     * @return
     */
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean updateById(MchInfo entity) {
        if(entity.getReflectPoints().compareTo(BigDecimal.ZERO) < 0){
            throw new ForbesException(PayBizResultEnum.MCH_POINTS.getBizCode(),PayBizResultEnum.MCH_POINTS.getBizMessage());
        }
        boolean isUp =  SqlHelper.retBool(baseMapper.updateById(entity));
        return isUp;
    }
}