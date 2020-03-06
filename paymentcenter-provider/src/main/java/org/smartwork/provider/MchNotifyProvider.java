package org.smartwork.provider;

import io.swagger.annotations.Api;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.IMchNotifyService;
import org.smartwork.dal.entity.MchNotify;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags={"商户通知管理"})
@RestController
@RequestMapping("/mchnotify")
public class MchNotifyProvider extends BaseProvider<IMchNotifyService, MchNotify> {
}