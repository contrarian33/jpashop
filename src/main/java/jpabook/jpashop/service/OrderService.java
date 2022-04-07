package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        /*
        * 소스코드가 퍼지면서 createOrderItem()메서드를 이용하지 않고, 아래와 같이 개발하는 사람이 생길 수 있음
        * OrderItem orderItem = new OrderItem();
        * orderItem.setCount(); (생성자 사용없이, setter를 이용해서 값을 세팅하는 방식)
        * 문제는 한쪽에서는 생성자사용방식, 한쪽에서는 setter사용방식으로 개발하기 때문에
        * 퍼지면 퍼질수록 유지보수에 어려움이 생김
        * ex) 생성로직을 변경할때 (생성로직에서 필드를 추가한다거나 로직에 수정이 생긴 경우)
        *
        * 따라서 생성자사용없이 setter로 생성하는 방식을 전부 막아야 한다.
        * 어떻게?
        * OrderItem의 constructor를 만들때 JPA는 protected까지 기본생성자를 만들 수 있게 허용해줌
        * ex) protected OrderItem() {}
        * JPA 사용하면서 protected 지정하는 것은 쓰지말라는 의미이기때문에
        * OrderItem orderItem = new OrderItem();
        * 생성시 에러가 발생, 사용하지 못하도록 제한한다는 의미전달이 가능
        *
        * lombok에서는 protected OrderItem() {}를 어노테이션을 이용해 줄일 수 있음
        * @NoArgsConstructor(access = AccessLevel.PROTECTED)
        * 를 이용하면
        * protected OrderItem() {} 생략 가능
        * */

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        // Order 엔티티 내부의 Delivery, OrderItem에 CascadeType.ALL 세팅이 되어있기 때문에
        // order.persist에 의해 자동으로 delivery.persist, orderItem.persist 호출이 됨

        // order가 delivery를 관리, order가 orderItem을 관리하는 수준에서 Cascade를 사용하는게 적절
        // 참조하는 주인이 private owner일 경우에만 사용해야 함
        // Delivery, OrderItem은 Order이외에 사용되는 지점이 없이 Order에서만 사용한다
        // 즉, 라이프사이클이 동일하게 관리되는 선상에서 Cascade를 사용해야 한다.
        // 또한, 다른것들이 참조할 수 없는 private owner인 경우에만 Cascade를 사용해야 한다.

        // Delivery, OrderItem이 중요하고, 다른 엔티티에서도 가져다가 사용 및 수정하는 케이스라면
        // Cascade 지정 없이, 별도의 Repository를 생성하고 각각 persist 하는 방식으로 사용하는게 맞다.

        return order.getId();

    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        /*
        cancel 내부 로직에 update 명령이 직접적으로 들어있지 않지만
        JPA는 데이터 값이 변하면 더티체킹을 통해 값이 변한 엔티티를 자동으로 update 쿼리를 날리기 때문에
        실제로 엔티티는 update가 된다
         */
    }

    /**
     * 검색
     */
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }
}
