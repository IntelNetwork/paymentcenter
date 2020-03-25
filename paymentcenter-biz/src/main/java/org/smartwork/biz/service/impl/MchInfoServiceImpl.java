package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.swagger.models.auth.In;
import org.forbes.comm.exception.ForbesException;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.comm.PayBizResultEnum;
import org.smartwork.constant.MchConstant;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.PayChannel;
import org.smartwork.dal.mapper.MchInfoMapper;
import org.smartwork.dal.mapper.PayChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Service
public class MchInfoServiceImpl extends ServiceImpl<MchInfoMapper, MchInfo> implements IMchInfoService {

    @Autowired
    PayChannelMapper payChannelMapper;

    /***
     * 删除对象
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        MchInfo mchInfo = baseMapper.selectById(id);
        Integer count=payChannelMapper.selectCount(new QueryWrapper<PayChannel>().eq(MchConstant.MCHID,id));
        if (mchInfo.getState().equals(MchStateEnum.ACTIVITY.getCode()) && count>0 ){
            throw new ForbesException(PayBizResultEnum.MCH_HAVE_RELEASED_DEL.getBizCode()
                    ,String.format(PayBizResultEnum.MCH_HAVE_RELEASED_DEL.getBizMessage()));
        }
        boolean delBool =  SqlHelper.retBool(baseMapper.deleteById(id));
        return delBool;
    }

    /**
     * 批量删除
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        List<MchInfo> mchInfos =  baseMapper.selectBatchIds(idList);
        mchInfos.stream().forEach(mchInfo -> {
            Integer count=payChannelMapper.selectCount(new QueryWrapper<PayChannel>().eq(MchConstant.MCHID,mchInfo));
            if (mchInfo.getState().equals(MchStateEnum.ACTIVITY.getCode()) && count>0){
                throw new ForbesException(PayBizResultEnum.MCH_HAVE_RELEASED_DEL.getBizCode()
                        ,String.format(PayBizResultEnum.MCH_HAVE_RELEASED_DEL.getBizMessage()));
            }
        });
        boolean delBool =  SqlHelper.retBool(baseMapper.deleteBatchIds(idList));
        return delBool;
    }
}