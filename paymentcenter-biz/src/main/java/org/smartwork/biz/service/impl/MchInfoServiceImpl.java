package org.smartwork.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.mapper.MchInfoMapper;
import org.springframework.stereotype.Service;

@Service
public class MchInfoServiceImpl extends ServiceImpl<MchInfoMapper, MchInfo> implements IMchInfoService {
}