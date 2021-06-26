package red.asuka.alice.commons.page;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Tomonori
 * @Date: 2019/12/9 15:40
 * @Title: 瀑布分页泛型类
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * -----
 */
@Data
@NoArgsConstructor
public class DataPage<T> implements Serializable {

    private Integer hasNext;    //是否还有下一页
    @JsonSerialize(using = ToStringSerializer.class)
    private Long    lastTime;   //下一页请求时间戳
    @JsonSerialize(using = ToStringSerializer.class)
    private Long    lastId;     //上一页请求的最后一条数据id
    private Double  lastScore;  //上一页最后的一条的分数
    private List<T> list;       //内容
    private Integer totalPage;  //总页数
    private Integer totalCount; //数据总条数

    public DataPage(Integer hasNext, Long lastTime, Long lastId, Double lastScore, List<T> list, Integer totalPage, Integer totalCount) {
        this.hasNext = hasNext;
        this.lastTime = lastTime;
        this.lastId = lastId;
        this.list = list;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.lastScore = lastScore;
    }

    public static <T> DataPage init(List<T> list, Integer hasNext, Long lastTime, Integer totalPage) {
        DataPage<T> page = new DataPage();
        page.setList(list);
        page.setHasNext(hasNext);
        page.setLastTime(lastTime);
        page.setTotalPage(totalPage);
        return page;
    }

    public static <T> Builder custom() {
        return new Builder();
    }

    //chain的中文含义是链式的，设置为true，则setter方法返回当前对象。如下
    @Accessors(chain = true)
    public static class Builder<T> {

        private Integer hasNext;        //是否还有下一页
        @JsonSerialize(using = ToStringSerializer.class)
        private Long lastTime;          //下一页请求时间戳
        @JsonSerialize(using = ToStringSerializer.class)
        private Long lastId;            //上一页请求的最后一条数据ID
        private Double lastScore;       //上一页最后一天数据的分数
        private List<T> list;           //内容
        private Integer totalPage;      //总页数
        private Integer totalCount;     //数据总条数

        public Builder hasNext(Integer hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public Builder lastTime(Long lastTime) {
            this.lastTime = lastTime;
            return this;
        }

        public Builder lastId(Long lastId) {
            this.lastId = lastId;
            return this;
        }

        public Builder lastScore(Double lastScore) {
            this.lastScore = lastScore;
            return this;
        }

        public Builder<T> list(List<T> list) {
            this.list = list;
            return this;
        }

        public Builder totalPage(Integer totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        public Builder totalCount(Integer totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public DataPage build() {
            return new DataPage(hasNext, lastTime, lastId, lastScore, list, totalPage, totalCount);
        }
    }
}
