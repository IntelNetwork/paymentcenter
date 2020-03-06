package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.smartwork.biz.service.IMchNotifyService;
import org.smartwork.dal.entity.MchNotify;
import org.smartwork.dal.mapper.MchNotifyMapper;
import org.springframework.stereotype.Service;

@Service
public class MchNotifyServiceImpl extends ServiceImpl<MchNotifyMapper, MchNotify> implements IMchNotifyService {
}