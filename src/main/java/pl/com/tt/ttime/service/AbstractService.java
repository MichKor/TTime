package pl.com.tt.ttime.service;

import java.io.Serializable;
import java.util.List;

public interface AbstractService<T, ID extends Serializable> {

    List<T> listAll();

    T save(T t);

    List<T> saveAll(Iterable<T> collection);

    void deleteById(ID id);

    void delete(T t);

    void deleteAll();

    T findById(ID id);
}
