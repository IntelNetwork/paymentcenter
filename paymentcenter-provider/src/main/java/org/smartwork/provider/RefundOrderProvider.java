package org.smartwork.provider;

import io.swagger.annotations.Api;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.IRefundOrderService;
import org.smartwork.dal.entity.RefundOrder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags={"退款订单管理"})
@RestController
@RequestMapping("/refundorder")
public class RefundOrderProvider extends BaseProvider<IRefundOrderService, RefundOrder> {
}