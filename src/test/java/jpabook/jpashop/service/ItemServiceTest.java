package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {
    
    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;

    @Test
    public void saveItem() throws Exception {
        //given
        Book book = new Book();
        book.setAuthor("kim");
        book.setIsbn("123");
        itemService.saveItem(book);

        //when
        Book result = (Book) itemService.findOne(book.getId());

        //then
        assertEquals(book, result);
    }

    @Test
    public void findItems() throws Exception {
        //given
        Book book1 = new Book();
        book1.setAuthor("kim1");
        book1.setIsbn("111");
        itemService.saveItem(book1);

        Book book2 = new Book();
        book2.setAuthor("kim2");
        book2.setIsbn("222");
        itemService.saveItem(book2);

        Book book3 = new Book();
        book3.setAuthor("kim3");
        book3.setIsbn("333");
        itemService.saveItem(book3);

        //when
        List<Item> resultList = itemService.findItems();

        //then
        assertEquals(3, resultList.size());
    }
}