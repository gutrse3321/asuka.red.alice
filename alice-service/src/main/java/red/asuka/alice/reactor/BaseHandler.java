package red.asuka.alice.reactor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.selector.Selector;
import reactor.function.Consumer;

import static reactor.event.selector.Selectors.$;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2020-12-04 15:34
 */
public abstract class BaseHandler<T> implements InitializingBean, Consumer<Event<T>> {

    /**
     * 事件处理池(异步处理)
     */
    @Autowired
    protected Reactor pool;

    /**
     * 事件处理队列(one by one)
     */
    @Autowired
    @Qualifier(value = "loop")
    protected Reactor loop;

    /**
     * 选择器-获取class的小驼峰字符串$()组合
     * @return
     */
    protected final Selector selector() {
        //reactor.on(********$("userHandler")*********, userHandler);
        return $(this.getClass().getSimpleName());
    }

    /**
     * 键选择器-获取class的小驼峰字符串
     * @return
     */
    protected final String selectorKey() {
        return this.getClass().getSimpleName();
    }

    /**
     * 事件的监听器，以便于接收发送的事件并处理。需要实现 Consumer<Event<T>> 接口，其中 T 是处理程序接收的数据类型
     * 封装公共使用
     * @param tEvent
     */
    @Override
    public void accept(Event<T> tEvent) {
        this.handler(tEvent.getData(), tEvent);
    }

    /**
     * 发送的事件绑定到指定的监听器
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.register();
    }

    /**
     * 封装公共发送事件方法
     * @param o
     */
    public void notify(T o) {
        pool.notify(selectorKey(), Event.wrap(o));
    }

    public final void notify(Event o) {
        pool.notify(selectorKey(), o);
    }

    protected abstract void handler(T o, Event<T> tEvent);

    protected void register() throws Exception {
        pool.on(selector(), this);
    }
}
