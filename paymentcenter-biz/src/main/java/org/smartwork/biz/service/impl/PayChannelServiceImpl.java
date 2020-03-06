package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.dal.entity.PayChannel;
import org.smartwork.dal.mapper.PayChannelMapper;
import org.springframework.stereotype.Service;

@Service
public class PayChannelServiceImpl extends ServiceImpl<PayChannelMapper, PayChannel> implements IPayChannelService {
}