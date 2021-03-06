package commands


import java.time.LocalDateTime

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory
import org.springframework.util.Assert

import epizza.delivery.order.DeliveryJob
import epizza.delivery.order.OrderServiceAssigmentClient

class select {

    @Usage("selects an open order")
    @Command
    def main(InvocationContext context) {

        Integer orderId = Integer.parseInt(context.readLine("Which order to select? ", true))
        Integer minutes = Integer.parseInt(context.readLine("Expected delivery time in minutes: ", true))

        BeanFactory beanFactory = context.attributes["spring.beanfactory"]
        Assert.notNull(beanFactory)

        OrderServiceAssigmentClient client = beanFactory.getBean(OrderServiceAssigmentClient)
        client.assignMyselfToOrder(orderId, new DeliveryJob("Joe the User", LocalDateTime.now().plusMinutes(minutes)))

        String result = "SUCCESS..."

        return result
    }

}