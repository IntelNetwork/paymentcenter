package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.forbes.comm.exception.ForbesException;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.comm.ChannelStateEnum;
import org.smartwork.comm.PayBizResultEnum;
import org.smartwork.dal.entity.PayChannel;
import org.smartwork.dal.mapper.PayChannelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Service
public class PayChannelServiceImpl extends ServiceImpl<PayChannelMapper, PayChannel> implements IPayChannelService {

    /***
     * 删除对象
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        PayChannel payChannel = baseMapper.selectById(id);
        if (payChannel.getState().equals(ChannelStateEnum.ACTIVITY.getCode())){
            throw new ForbesException(PayBizResultEnum.PAYCHANNEL_HAVE_RELEASED_DEL.getBizCode()
                    ,String.format(PayBizResultEnum.PAYCHANNEL_HAVE_RELEASED_DEL.getBizMessage()));
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
        List<PayChannel> payChannels =  baseMapper.selectBatchIds(idList);
        payChannels.stream().forEach(payChannel -> {
            if (payChannel.getState().equals(ChannelStateEnum.ACTIVITY.getCode())){
                throw new ForbesException(PayBizResultEnum.PAYCHANNEL_HAVE_RELEASED_DEL.getBizCode()
                        ,String.format(PayBizResultEnum.PAYCHANNEL_HAVE_RELEASED_DEL.getBizMessage()));
            }
        });
        boolean delBool =  SqlHelper.retBool(baseMapper.deleteBatchIds(idList));
        return delBool;
    }
}