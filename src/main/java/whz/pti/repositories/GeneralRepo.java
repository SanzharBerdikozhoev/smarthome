package whz.pti.repositories;

import java.sql.SQLException;
import java.util.Optional;

public interface GeneralRepo<T> {
    public Optional<T> getById(Long id) throws SQLException;
    public Optional<T> getByField(String field, Object value);
    public Iterable<T> getAll();
    public Iterable<T> getPage(int from, int size);

    public T save(T entity);
    public Iterable<T> saveAll(Iterable<T> entities);

    public T update(T newEntity, T entityToUpdate);
    public T updateById(T newEntity, Long id);

    public void delete(T entity);
    public void deleteById(Long id);
}
