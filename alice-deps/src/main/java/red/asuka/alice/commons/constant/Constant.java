package red.asuka.alice.commons.constant;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-19 17:18
 */
public class Constant {

    public static final String LICENSE = "aw_alice";

    public enum DataState {
        Invalid(0),   //删除
        Disable(1),   //无效
        Available(2); //有效

        private Integer val;

        DataState(Integer val) {
            this.val = val;
        }

        public DataState getType(Integer val) {
            this.val = val;
            return this;
        }

        public Integer getVal() {
            return val;
        }
    }

    public enum YesNo {
        FALSE,
        TRUE
    }

    public enum DriversOs {
        Android,
        iOS,
        Web
    }

    /**
     * sql公共符号
     */
    public enum SqlCommonMethod {
        Equal("="),
        UnEqual("!="),
        GreaterThan(">"),
        LessThan("<"),
        GreaterThanOrEqual(">="),
        LessThanOrEqual("<="),
        Instr("instr");

        private String val;

        SqlCommonMethod(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    /**
     * 成功信息
     */
    public enum MsgData {
        Get("获取成功"),
        Add("添加成功"),
        Edit("修改成功"),
        Del("删除成功"),
        Err("操作失败"),
        Send("下发成功"),
        Opt("操作成功");


        private String val;

        MsgData(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    /**
     * 空间类型
     */
    public enum SpaceType {
        Village(0, "Village"),     //小区
        Build(1, "Build"),         //楼栋
        Util(2, "Util"),           //单元
        Source(3, "Source"),       //房源
        Room(4, "Room"),           //房间
        Common(5, "Common"),       //公区
        Unknown(-1, "Unknown");    //未知

        private int val;
        private String type;

        SpaceType(Integer val, String type) {
            this.val = val;
            this.type = type;
        }

        public Integer getVal() {
            return val;
        }

        public String getType() {
            return type;
        }

        public static SpaceType getType(int val) {
            for (SpaceType item : values()) {
                if (item.getVal().equals(val)) {
                    return item;
                }
            }
            return Unknown;
        }
    }

    public enum Gender {
        Other,      //未知
        Male,        //男
        Female       //女
    }
}
