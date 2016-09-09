package epizza.order;

import static javax.persistence.GenerationType.IDENTITY;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

import javax.money.MonetaryAmount;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.javamoney.moneta.Money;

import com.google.common.collect.Lists;

@Entity
@Access(AccessType.FIELD)
@Table(name = "PIZZA_ORDER")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
@ToString(of = { "id", "orderItems" })
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Basic
    @Column(name = "ORDERED_AT", nullable = false)
    private LocalDateTime orderedAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Basic
    @Column(name = "ETD", nullable = true)
    private LocalDateTime estimatedTimeOfDelivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LineItem> orderItems = Lists.newArrayList();

    @Basic
    @Column(name = "COMMENT", length = 255)
    private String comment;

    @Embedded
    private Address deliveryAddress;

    public void setOrderItems(List<LineItem> items) {
        this.orderItems.clear();
        items.forEach(this::addOrderItem);
    }

    public void addOrderItem(LineItem item) {
        item.setOrder(this);
        this.orderItems.add(item);
    }

    public MonetaryAmount getTotalPrice() {
        return orderItems.stream()
                .map(LineItem::getPrice)
                .reduce(MonetaryAmount::add)
                .orElse(Money.of(0.0, "EUR"));
    }
}