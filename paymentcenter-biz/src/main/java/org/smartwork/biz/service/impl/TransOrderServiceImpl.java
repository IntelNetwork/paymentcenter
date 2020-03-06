package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.smartwork.biz.service.ITransOrderService;
import org.smartwork.dal.entity.TransOrder;
import org.smartwork.dal.mapper.TransOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class TransOrderServiceImpl extends ServiceImpl<TransOrderMapper, TransOrder> implements ITransOrderService {
}