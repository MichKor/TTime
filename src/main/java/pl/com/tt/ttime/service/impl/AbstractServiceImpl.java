package pl.com.tt.ttime.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.tt.ttime.service.AbstractService;

import java.io.Serializable;
import java.util.List;


public abstract class AbstractServiceImpl<T, ID extends Serializable, U extends JpaRepository<T, ID>>
        implements AbstractService<T, ID> {

    protected U repository;

    @SuppressWarnings("WeakerAccess")
    protected AbstractServiceImpl(U repository) {
        this.repository = repository;
    }

    @Override
    public List<T> listAll() {
        return repository.findAll();
    }

    @Override
    public T findById(ID id) {
        return repository.findOne(id);
    }

    @Override
    public T save(T t) {
        return repository.save(t);
    }

    @Override
    public void deleteById(ID id) {
        repository.delete(id);
    }

    @Override
    public void delete(T t) {
        repository.delete(t);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<T> saveAll(Iterable<T> collection) {
        return repository.save(collection);
    }
}
