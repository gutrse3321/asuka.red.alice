package red.asuka.alice.datasource.jpa.config;

import red.asuka.alice.commons.constant.Constant;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author: Tomonori
 * @Date: 2019/11/8 17:21
 * @Desc: JPA基础Repository抽象类
 */
@NoRepositoryBean
public interface ISimpleRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 标准查询构造器
     * @return
     */
    CriteriaBuilderImpl extBuilder();

    <D> List<D> extExecuteCQ(CriteriaQuery<D> cq);

    <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, Pageable pageable);

    <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, int page, int size);

    /**
     * 标准查询修改
     * @param cu
     * @param <D>
     * @return
     */
    <D> int extExecuteCU(CriteriaUpdate<D> cu);

    /**
     * 标准查询删除
     * @param cd
     * @param <D>
     * @return
     */
    <D> int extExecuteCD(CriteriaDelete<D> cd);

    /**
     * 完整保存，如果有空字段，将设置为NULL
     * @param entity
     * @param <S>
     * @return
     * @throws Exception
     */
    <S extends T> S extSaveFull(S entity) throws Exception;

    /**
     * 保留非空字段
     * @param entity
     * @param <S>
     * @return
     * @throws Exception
     */
    <S extends T> S extSaveNotNull(S entity) throws Exception;

    /**
     * 保留非空字段
     * @param entities
     * @param <S>
     * @return
     * @throws Exception
     */
    <S extends T> List<S> extSaveAllNot(Iterable<S> entities) throws Exception;

    /**
     * 软删除
     * @param id
     * @return
     */
    int extDeleteBySoft(ID id, String... account);

    /**
     * 物理删除
     * @param id
     * @return
     */
    int extDeleteByPhysically(ID id);

    /**
     * 禁用
     * @param id
     * @return
     */
    int extDisable(ID id, String update_by);

    /**
     * 启用
     *
     * @param id
     * @return
     */
    int extAvailable(ID id, String update_by);

    /**
     * 刷新修改时间
     * @param id
     * @param time
     * @return
     */
    int extRefreshUpdateTime(ID id, Long time);

    int extRefreshUpdateTime(ID id, String time);

    /**
     * 完整修改，如果有空字段，将设置为null
     * @param entity
     * @param <S>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    <S extends T> int extUpdateFull(S entity) throws InvocationTargetException, IllegalAccessException;

    /**
     * 修改非空字段
     * @param entity
     * @param <S>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    <S extends T> int extUpdateNotNull(S entity) throws InvocationTargetException, IllegalAccessException;

    T extFindOne(ID id);

    T extFindOne(ID id, Constant.DataState state);

    /**
     * 原生SQL查询
     * @param sql
     * @param clazz
     * @param parameter
     * @param <D>
     * @return
     */
    <D> List<D> extFindByNativeSQL(String sql, Class<D> clazz, List<Object> parameter);

    /**
     * 插入实体
     * @param entity
     * @param <S>
     * @return
     */
    <S extends T> S saveEntity(S entity);
}
