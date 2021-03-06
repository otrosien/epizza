package epizza.order.orderstatus;

import epizza.order.Order;
import epizza.order.OrderService;
import epizza.order.OrderStatus;
import epizza.shared.event.AbstractEventSubscriber;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@Slf4j
public abstract class OrderStatusEventSubscriber extends AbstractEventSubscriber {

    private final OrderService orderService;
    private final OrderStatus orderStatus;

    protected OrderStatusEventSubscriber(OrderService orderService,
                                         ObjectMapper objectMapper,
                                         String type, OrderStatus orderStatus) {
        super(objectMapper, type);
        this.orderService = orderService;
        this.orderStatus = orderStatus;
    }

    @Override
    protected void handleOwnType(Map<String, Object> event) {
        Map<String, Object> payload = getPayload(event);
        Long orderId = getOrderIdFromPayload(payload, event);
        Order order = orderService.getOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order %s not found", orderId)));

        enhanceOrder(order, payload);

        order.setStatus(orderStatus);
        orderService.update(order);
    }

    protected void enhanceOrder(Order order, Map<String, Object> payload) {
        //add logic in implementation if needed
    };

    private Long getOrderIdFromPayload(Map<String, Object> payload, Map<String, Object> event) {
        String orderUriString = (String) payload.get("orderLink");
        if (Strings.isNullOrEmpty(orderUriString)) {
            log.error("Event {} does not contain an orderLink", event);
            throw new IllegalArgumentException(String.format("Event %s does not contain an orderLink", event));
        }
        URI orderUri = URI.create(orderUriString);

        String[] pathItems = orderUri.getPath().split("/");
        if (pathItems.length > 1) {
            String idPart = pathItems[pathItems.length - 1];
            return Long.valueOf(idPart);
        } else {
            throw new IllegalArgumentException(String.format("orderLink %s in event %s does not contain an id", orderUriString, event));
        }
    }
}
