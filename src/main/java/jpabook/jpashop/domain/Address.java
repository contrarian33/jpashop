package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // 값 없이 생성하지 못하도록 protected로 기본생성자 지정
    protected Address() { }

    // 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스를 만든다
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
