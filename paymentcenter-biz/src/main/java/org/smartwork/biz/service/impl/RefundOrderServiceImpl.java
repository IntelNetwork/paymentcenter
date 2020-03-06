package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.smartwork.biz.service.IRefundOrderService;
import org.smartwork.dal.entity.RefundOrder;
import org.smartwork.dal.mapper.RefundOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class RefundOrderServiceImpl extends ServiceImpl<RefundOrderMapper, RefundOrder> implements IRefundOrderService {
}