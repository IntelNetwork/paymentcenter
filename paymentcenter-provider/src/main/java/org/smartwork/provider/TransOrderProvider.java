package org.smartwork.provider;

import io.swagger.annotations.Api;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.ITransOrderService;
import org.smartwork.dal.entity.TransOrder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags={"转账订单管理"})
@RestController
@RequestMapping("/transorder")
public class TransOrderProvider extends BaseProvider<ITransOrderService, TransOrder> {
}