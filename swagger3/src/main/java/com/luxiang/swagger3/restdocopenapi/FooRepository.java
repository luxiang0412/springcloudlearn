package com.luxiang.swagger3.restdocopenapi;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author luxiang
 * description  //TODO
 * create       2020-07-01 16:27
 */
@Repository
public interface FooRepository extends PagingAndSortingRepository<Foo, Long> {

}
