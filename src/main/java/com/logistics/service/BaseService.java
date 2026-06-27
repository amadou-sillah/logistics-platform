package com.logistics.service;

import com.logistics.exception.ResourceNotFoundException;
import com.logistics.repository.BaseRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseService<T, ID> {
    protected final BaseRepository<T, ID> repository;

    public T save(T entity) {
        return repository.save(entity);
    }

    public T update(ID id, T entity) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getEntityName(), "id", id.toString());
        }
        return repository.save(entity);
    }

    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getEntityName(), "id", id.toString());
        }
        repository.deleteById(id);
    }

    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    protected abstract String getEntityName();
}
