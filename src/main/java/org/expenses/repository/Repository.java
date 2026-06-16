package org.expenses.repository;

import java.util.List;

public interface Repository<T> {

    void save(T t);
    T getById(Integer id);
    void deleteById(Integer id) ;
    List<T> listAll();

}
