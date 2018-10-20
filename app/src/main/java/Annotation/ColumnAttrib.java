package Annotation;


public enum ColumnAttrib {
	@Description("自增ID")
    IDENTITY,

    @Description("主键约束")
    PRIMARYKEY,

    @Description("外键约束")
    FOREIGNKEY,
    @Description("扩展，不属于表字段")
    ExtendCol
}
