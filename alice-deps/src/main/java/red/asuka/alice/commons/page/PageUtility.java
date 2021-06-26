package red.asuka.alice.commons.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-22 10:26
 *
 * 自定义分页类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageUtility<T> implements Serializable {

    private Integer limitStart; //数据库中搜索起始位置
    private Integer totalPages; //总页数
    private Integer totalElements;  //总元素个数
    private Integer numberOfElements; //content中元素个数
    private Integer number = 0; //当前第几页 从0开始
    private Integer size = 10;   //当前页允许最大元素个数
    private List<T> content; //内容


    public  Integer getTotalPages(Integer totalElements, Integer size){
        return (totalElements == 0 || totalElements<=size) ? 1 : (totalElements%size == 0 ? totalElements/size : totalElements/size+1);
    }

    public Integer getNumberOfElements(List<T> content){
        return (content == null || content.size() == 0) ? 0 : content.size();
    }

    public static<T> PageUtility init(List<T> content, Integer totalElements, Integer number, Integer size){
        PageUtility<T> page = new PageUtility<>();
        totalElements = totalElements == null ? 0 : totalElements;
        number = number == null ? 0 : number;
        size = size == null ? 0 : size;
        page.setContent(content);
        page.setTotalPages(page.getTotalPages(totalElements, size));
        page.setTotalElements(totalElements);
        page.setNumberOfElements(page.getNumberOfElements(content));
        page.setNumber(number);
        page.setSize(size);
        return page;
    }
}

