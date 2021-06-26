package red.asuka.alice.datasource.jpa.config;

import red.asuka.alice.commons.constant.Constant;
import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.commons.model.EntityPropertyInfo;
import red.asuka.alice.datasource.jpa.entity.BaseEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: Tomonori
 * @Date: 2019/11/11 10:30
 * @Desc: JPA 基础Repository实现类
 */
@Transactional
public class SimpleRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ISimpleRepository<T, ID> {

    /**
     * 实体管理器接口，操作实体的方法
     * EntityManager实例与一个持久化上下文关联。在持久性上下文中，管理实体实例及其生命周期。
     * 该EntityManager API用于创建和删除持久实体实例，通过其主键查找实体以及查询实体。
     */
    protected final EntityManager em;
    /**
     * 实体信息
     */
    protected final JpaEntityInformation information;
    /**
     * Session不是线程安全的，操作实体
     */
    protected final Session session;


    /*******************************
     * constructor methods
     *******************************/
    /**
     * 两个构造器都是继承 SimpleJpaRepository<T, ID> 所需
     * @param domainClass
     * @param em
     */
    public SimpleRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        //使用JpaEntityInformationSupport从其类中获取实体信息
        this.information = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
        this.session = (Session) em.getDelegate();
    }

    public SimpleRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
        this.information = entityInformation;
        this.session = (Session) entityManager.getDelegate();
    }


    /*******************************
     * private methods
     *******************************/
    /**
     * 在save方法保存实体的时候填充创建时间，保存时间为当前时间的时间戳
     * @param entity
     */
    private void fillTime(T entity) {
        Long time = System.currentTimeMillis();
        BaseEntity baseEntity = (BaseEntity) entity;
        if (baseEntity.getCreated_time() == null) {
            baseEntity.setCreated_time(time);
            baseEntity.setUpdated_time(time);
        }
        baseEntity.setUpdated_time(time);
    }


    /*******************************
     * public methods
     *******************************/
    /**
     * 保存操作
     *
     * 重载SimpleJpaRepository的save方法
     * 如果传入的泛型为BaseEntity的实例，则填充创建时间
     * @param entity
     * @param <S>
     * @return
     */
    @Override
    public <S extends T> S save(S entity) {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }
        return super.save(entity);
    }

    /**
     * 返回list类型修改成saveAll方法，公司项目1.5.3为save方法，且源码实现为Assert.notnull判断验证
     * @param entities
     * @param <S>
     * @return
     */
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        ArrayList result = new ArrayList();
        if (entities != null) {
            Iterator var3 = entities.iterator();
            while (var3.hasNext()) {
                S entity = (S) var3.next();
                result.add(this.save(entity));
            }
        }
        return result;
    }

    @Override
    public <S extends T> List<S> extSaveAllNot(Iterable<S> entities) throws Exception {
        ArrayList result = new ArrayList();
        if (entities != null) {
            Iterator var3 = entities.iterator();
            while (var3.hasNext()) {
                S entity = (S) var3.next();
                result.add(this.extSaveNotNull(entity));
            }
        }
        return result;
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }
        return super.saveAndFlush(entity);
    }

    /**
     * 不填充创建时间
     * @param entity
     * @param <S>
     * @return
     */
    @Override
    public <S extends T> S saveEntity(S entity) {
        return super.save(entity);
    }

    /**
     * 完整保存，如果有空字段，将设置为NULL
     * 实体持久化
     * @param entity
     * @param <S>
     * @return
     * @throws Exception
     */
    @Override
    public <S extends T> S extSaveFull(S entity) throws Exception {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }

        //实体信息接口,是否持久化操作过，是的话修改实体属性，否的话创建持久化
        //返回是否将给定实体视为新实体
        if (information.isNew(entity)) {
            //创建实体持久化
            em.persist(entity);
            return entity;
        } else {
            //完整修改，如果有空字段，将设置为null
            int i = this.extUpdateFull(entity);
            //修改失败的话，抛出异常信息，回滚操作
            if (i == 0) throw EXPF.E404(this.getClass().getSimpleName(), true);
            return entity;
        }
    }

    /**
     * 保留非空字段
     * @param entity
     * @param <S>
     * @return
     * @throws Exception
     */
    @Override
    public <S extends T> S extSaveNotNull(S entity) throws Exception {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }

        //实体信息接口,是否持久化操作过，是的话修改实体属性，否的话创建持久化
        //返回是否将给定实体视为新实体
        if (information.isNew(entity)) {
            //创建实体持久化
            em.persist(entity);
            return entity;
        } else {
            //修改非空字段
            int i = this.extUpdateNotNull(entity);
            if (i == 0) throw EXPF.E404(this.getClass().getSimpleName(), true);
            return entity;
        }
    }

    /**
     * 标准查询构造器
     * @return
     */
    @Override
    public CriteriaBuilderImpl extBuilder() {
        //getCriteriaBuilder() 返回的实例CriteriaBuilder以创建 CriteriaQuery对象
        //CriteriaBuilder接口 用于构造 条件查询，复合查询，排序等等
        //CriteriaQuery接口 定义了特定于顶级查询的功能
        //如果实体管理者已关闭，抛出IllegalStateException异常
        return (CriteriaBuilderImpl) em.getCriteriaBuilder();
    }

    //条件查询
    //CriteriaQuery接口 定义了特定于顶级查询的功能
    @Override
    public <D> List<D> extExecuteCQ(CriteriaQuery<D> cq) {
        return em.createQuery(cq).getResultList();
    }

    //分页查询 Pageable接口对象分页信息
    //Pageable 分页信息的抽象接口
    @Override
    public <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, Pageable pageable) {
        //用于控制类型化查询的执行的接口
        TypedQuery<D> query = em.createQuery(cq);
        if (null != pageable) {
            //setFirstResult(int) 设置要检索的第一个结果的位置
            //getOffset() 返回根据基础页面和页面大小要采用的偏移量
            query.setFirstResult((int) pageable.getOffset());
            //setMaxResults(int) 设置要检索的最大结果数
            //getPageSize() 返回要返回的项目数
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }

    //分页查询 直接分页信息传参构造
    @Override
    public <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, int page, int size) {
        //用于控制类型化查询的执行的接口
        TypedQuery<D> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(page);
        return query.getResultList();
    }

    /**
     * 标准查询修改
     * CriteriaUpdate接口定义了使用Criteria API执行批量更新操作的功能
     * Criteria API批量更新操作直接映射到数据库更新操作，从而绕开了任何乐观锁定检查。
     * 如果需要，使用批量更新操作的可移植应用程序必须手动更新version列的值，和/或手动验证version列的值。
     * 持久性上下文与批量更新的结果不同步。
     * 一个CriteriaUpdate对象必须具有单个根
     * @param cu
     * @param <D>
     * @return
     */
    @Override
    public <D> int extExecuteCU(CriteriaUpdate<D> cu) {
        return em.createQuery(cu).executeUpdate();
    }

    /**
     * 标准查询删除
     * @param cd
     * @param <D>
     * @return
     */
    @Override
    public <D> int extExecuteCD(CriteriaDelete<D> cd) {
        return em.createQuery(cd).executeUpdate();
    }

    /**
     * 软删除 修改dataState
     * @param id
     * @return
     */
    @Override
    public int extDeleteBySoft(ID id, String... account) {
        //创建标准查询构造器实例
        CriteriaBuilderImpl builder = this.extBuilder();
        //创建批量更新的CriteriaUpdate接口
        //getDomainClass() 继承SimpleJpaRepository类中的JpaEntityInformation泛型接口获取的泛型实体信息的JavaType
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        //创建并添加与作为更新目标的实体相对应的查询根
        Root<T> from = cu.from(getDomainClass());

        //修改的属性，将数据状态修改为删除的code，更新时间为当前时间戳
        cu.set("data_state", Constant.DataState.Invalid.ordinal());
        cu.set("updated_time", System.currentTimeMillis());
        if (account.length > 0) {
            cu.set("updated_by", account[0]);
        }

        //获取实体的属性信息（所有属性）
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        //条件，ID与获取的实体信息的显示名相同的条件
        Predicate where = builder.equal(from.get(propertyInfo.getPrimaryKey().getDisplayName()), id);
        cu.where(where);

        //执行修改方法
        return this.extExecuteCU(cu);
    }

    /**
     * 物理删除
     * @param id
     * @return
     */
    @Override
    public int extDeleteByPhysically(ID id) {
        //创建标准查询构造器实例
        CriteriaBuilderImpl builder = this.extBuilder();
        //创建批量删除操作的CriteriaDelete接口
        CriteriaDelete<T> cd = builder.createCriteriaDelete(getDomainClass());
        //创建并添加与作为删除目标的实体相对应的查询根
        Root<T> from = cd.from(getDomainClass());

        //获取实体的属性信息（所有属性）
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        //条件，ID与获取的实体信息的显示名相同的条件
        Predicate where = builder.equal(from.get(propertyInfo.getPrimaryKey().getDisplayName()), id);
        cd.where(where);

        //执行删除方法(从数据库抹除
        return this.extExecuteCD(cd);
    }

    /**
     * 禁用 实现与软删除一样，修改状态和更新时间
     * @param id
     * @return
     */
    @Override
    public int extDisable(ID id, String account) {
        //创建标准查询构造器实例
        CriteriaBuilderImpl builder = this.extBuilder();
        //创建批量更新的CriteriaUpdate接口
        //getDomainClass() 继承SimpleJpaRepository类中的JpaEntityInformation泛型接口获取的泛型实体信息的JavaType
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        //创建并添加与作为更新目标的实体相对应的查询根
        Root<T> from = cu.from(getDomainClass());

        //修改的属性，将数据状态修改为无效的code，更新时间为当前时间戳
        cu.set("data_state", Constant.DataState.Disable.ordinal());
        cu.set("updated_time", System.currentTimeMillis());
        cu.set("updated_by", account);

        //获取实体的属性信息（所有属性）
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        //条件，ID与获取的实体信息的显示名相同的条件
        Predicate where = builder.equal(from.get(propertyInfo.getPrimaryKey().getDisplayName()), id);
        cu.where(where);

        //执行修改方法
        return this.extExecuteCU(cu);
    }

    /**
     * 启用 实现与软删除一样，修改状态和更新时间
     * @param id
     * @return
     */
    @Override
    public int extAvailable(ID id, String account) {
        //创建标准查询构造器实例
        CriteriaBuilderImpl builder = this.extBuilder();
        //创建批量更新的CriteriaUpdate接口
        //getDomainClass() 继承SimpleJpaRepository类中的JpaEntityInformation泛型接口获取的泛型实体信息的JavaType
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        //创建并添加与作为更新目标的实体相对应的查询根
        Root<T> from = cu.from(getDomainClass());

        //修改的属性，将数据状态修改为有效的code，更新时间为当前时间戳
        cu.set("data_state", Constant.DataState.Available.ordinal());
        cu.set("updated_time", System.currentTimeMillis());
        cu.set("updated_by", account);

        //获取实体的属性信息（所有属性）
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        //条件，ID与获取的实体信息的显示名相同的条件
        Predicate where = builder.equal(from.get(propertyInfo.getPrimaryKey().getDisplayName()), id);
        cu.where(where);

        //执行修改方法
        return this.extExecuteCU(cu);
    }

    /**
     * 更新修改时间 实现与软删除类似，但只修改更新时间（传参指定时间）
     * @param id
     * @param time
     * @return
     */
    @Override
    public int extRefreshUpdateTime(ID id, Long time) {
        //创建标准查询构造器实例
        CriteriaBuilderImpl builder = this.extBuilder();
        //创建批量更新的CriteriaUpdate接口
        //getDomainClass() 继承SimpleJpaRepository类中的JpaEntityInformation泛型接口获取的泛型实体信息的JavaType
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        //创建并添加与作为更新目标的实体相对应的查询根
        Root<T> from = cu.from(getDomainClass());

        //修改的属性，将更新时间为传参time的值
        cu.set("update_time", time);

        //获取实体的属性信息（所有属性）
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        //条件，ID与获取的实体信息的显示名相同的条件
        Predicate where = builder.equal(from.get(propertyInfo.getPrimaryKey().getDisplayName()), id);
        cu.where(where);

        //执行修改方法
        return this.extExecuteCU(cu);
    }

    //装箱转换String类型的时间传参为Long类型
    @Override
    public int extRefreshUpdateTime(ID id, String time) {
        return this.extRefreshUpdateTime(id, Long.valueOf(time));
    }

    /**
     * 完整修改，如果有空字段，将设置为null
     * 必须是继承泛型T类型的或者T类型的类，使用方法
     * @param entity
     * @param <S>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public <S extends T> int extUpdateFull(S entity) throws InvocationTargetException, IllegalAccessException {
        //实例为BaseEntity填充创建时间
        if (entity instanceof BaseEntity) {
            this.fillTime(entity);
        }

        //获取entity的运行时的类型类，所有的类型类都是Class类的实例
        //可以调用其中的一些方法获得类型的信息
        Class<S> clazz = (Class<S>) entity.getClass();
        //创建标准查询构造器实例
        CriteriaBuilder builder = this.extBuilder();
        //创建批量更新的CriteriaUpdate接口
        //clazz 此实体的Class<entity Type>类型类实例
        CriteriaUpdate<S> cu = builder.createCriteriaUpdate(clazz);
        //创建并添加与作为更新目标的实体相对应的查询根
        Root<S> from = cu.from(clazz);

        //获取实体信息
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(clazz, true);
        Assert.notNull(propertyInfo, "PropertyDescriptor is not null");
        //获取属性信息每个属性一个数组元素
        PropertyDescriptor[] otherKey = propertyInfo.getOtherKey();
        //set标准修改器修改的属性和值（所有字段）
        for (PropertyDescriptor pro : otherKey) {
            //获取属性getter方法
            Method readMethod = pro.getReadMethod();
            //执行此(entity)类属性getter方法
            Object invoke = readMethod.invoke(entity);
            //set标准修改器修改的属性和值
            cu.set(pro.getName(), invoke);
        }

        PropertyDescriptor primaryKey = propertyInfo.getPrimaryKey();
        Method readMethod = primaryKey.getReadMethod();
        Object invoke = readMethod.invoke(entity);
        Assert.notNull(invoke, "Modify the object [id] is empty");
        //TODO 在JpaEntityPropertyUtility获取的实体信息，primarykey有赋值，checkAnnotation检查是否有Id和EmbeddedId注解的属性
        //TODO 而EntityPropertyUtility中没有做检查，故始终为null。可以做类似JpaEntityPropertyUtility这样的处理，针对某个属性赋值
        //则，这里实体信息primarykey就是主键的存在，做判断
        Predicate where = builder.equal(from.get(primaryKey.getName()), invoke);
        cu.where(where);

        return this.extExecuteCU(cu);
    }

    /**
     * 修改非空字段 与extUpdateFull方法实现大体一致，JpaEntityPropertyUtility.getProperty获取实体信息的时候不获取为null字段
     * @param entity
     * @param <S>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public <S extends T> int extUpdateNotNull(S entity) throws InvocationTargetException, IllegalAccessException {
        //实例为BaseEntity填充创建时间
        if (entity instanceof BaseEntity) {
            this.fillTime(entity);
        }

        //获取entity的运行时的类型类，所有的类型类都是Class类的实例
        //可以调用其中的一些方法获得类型的信息
        Class<S> clazz = (Class<S>) entity.getClass();
        //创建标准查询构造器实例
        CriteriaBuilder builder = this.extBuilder();
        //创建批量更新的CriteriaUpdate接口
        //clazz 此实体的Class<entity Type>类型类实例
        CriteriaUpdate<S> cu = builder.createCriteriaUpdate(clazz);
        //创建并添加与作为更新目标的实体相对应的查询根
        Root<S> from = cu.from(clazz);

        //获取实体信息
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(clazz, false);
        Assert.notNull(propertyInfo, "PropertyDescriptor is not null");
        //获取属性信息每个属性一个数组元素
        PropertyDescriptor[] otherKey = propertyInfo.getOtherKey();
        //set标准修改器修改的属性和值（所有字段）
        for (PropertyDescriptor pro : otherKey) {
            //获取属性getter方法
            Method readMethod = pro.getReadMethod();
            //执行此(entity)类属性getter方法
            Object invoke = readMethod.invoke(entity);
            //entity属性pro getter到的值不为null set标准修改器修改的属性和值
            if (null != invoke) cu.set(pro.getName(), invoke);
        }

        PropertyDescriptor primaryKey = propertyInfo.getPrimaryKey();
        Method readMethod = primaryKey.getReadMethod();
        Object invoke = readMethod.invoke(entity);
        Assert.notNull(invoke, "Modify the object [id] is empty");
        //TODO 在JpaEntityPropertyUtility获取的实体信息，primarykey有赋值，checkAnnotation检查是否有Id和EmbeddedId注解的属性
        //TODO 而EntityPropertyUtility中没有做检查，故始终为null。可以做类似JpaEntityPropertyUtility这样的处理，针对某个属性赋值
        //则，这里实体信息primarykey就是主键的存在，做判断
        Predicate where = builder.equal(from.get(primaryKey.getName()), invoke);
        cu.where(where);

        return this.extExecuteCU(cu);
    }

    /**
     * 查询单个
     * 一次执行多次查询来统计某些信息，这时为了保证数据整体的一致性，要用只读事务（Transactional(readOnly = true)）
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    @Override
    public T extFindOne(ID id) {
        //2.0之前 使用 findOne(ID)
        return super.findById(id).get();
    }

    @Override
    public T extFindOne(ID id, Constant.DataState state) {
        //创建标准查询构造器实例
        CriteriaBuilderImpl builder = this.extBuilder();
        //获取主类实体属性信息（全部
        EntityPropertyInfo propertyInfo = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        //创建批量查询的CriteriaQuery接口
        CriteriaQuery<T> query = builder.createQuery(getDomainClass());
        //创建并添加与作为查询目标的实体相对应的查询根
        Root<T> from = query.from(getDomainClass());

        //查询条件
        //dataState属性值与传参state一致的
        Predicate whereDataState = builder.equal(from.get("data_state"), state.ordinal());
        //id属性值与泛型ID值一致的
        Predicate whereId = builder.equal(from.get(propertyInfo.getPrimaryKey().getName()), id);
        query.where(whereId, whereDataState);

        //查询与条件一致的所有项
        List<T> ts = this.extExecuteCQ(query);
        //判断list是否为空，是返回null，否返回list第一个匹配项
        return CollectionUtils.isEmpty(ts) ? null : ts.get(0);
    }

    /**
     * 原生SQL查询
     * TODO 暂且不仔细查询每条代码意思
     * @param sql
     * @param clazz
     * @param parameter
     * @param <D>
     * @return
     */
    @Override
    public <D> List<D> extFindByNativeSQL(String sql, Class<D> clazz, List<Object> parameter) {
        Assert.notNull(sql, "NativeSql is not null");
        SQLQuery cq = session.createSQLQuery(sql);
        if (null != clazz) {
            addSclar(cq, clazz);
        }

        int i = 0;
        for (Object obj : parameter) {
            cq.setParameter(i++, obj);
        }

        return cq.list();
    }

    /***********************
     * static methods
     * @param query
     * @param clazz
     **********************/
    public static void addSclar(SQLQuery query, Class<?> clazz) {
        Assert.notNull(query, "Query sql required");
        Assert.notNull(clazz, "Entity class required");
        PropertyDescriptor[] propertyDescriptor = JpaEntityPropertyUtility.getEntityPropertyDescriptor(clazz);
        for (PropertyDescriptor descriptor : propertyDescriptor) {
            String name = descriptor.getName();
            Class<?> propertyType = descriptor.getPropertyType();
            if (propertyType == long.class || propertyType == Long.class) {
                query.addScalar(name, LongType.INSTANCE);
            } else if (propertyType == int.class || propertyType == Integer.class) {
                query.addScalar(name, IntegerType.INSTANCE);
            } else if ((propertyType == char.class) || (propertyType == Character.class)) {
                query.addScalar(name, CharacterType.INSTANCE);
            } else if ((propertyType == short.class) || (propertyType == Short.class)) {
                query.addScalar(name, ShortType.INSTANCE);
            } else if ((propertyType == double.class) || (propertyType == Double.class)) {
                query.addScalar(name, DoubleType.INSTANCE);
            } else if ((propertyType == float.class) || (propertyType == Float.class)) {
                query.addScalar(name, FloatType.INSTANCE);
            } else if ((propertyType == boolean.class) || (propertyType == Boolean.class)) {
                query.addScalar(name, BooleanType.INSTANCE);
            } else if (propertyType == String.class) {
                query.addScalar(name, StringType.INSTANCE);
            } else if (propertyType == java.sql.Date.class) {
                query.addScalar(name, DateType.INSTANCE);
            } else if (propertyType == BigDecimal.class) {
                query.addScalar(name, BigDecimalType.INSTANCE);
            }
        }
        query.setResultTransformer(Transformers.aliasToBean(clazz));
    }
}
