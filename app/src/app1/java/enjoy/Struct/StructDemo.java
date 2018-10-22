package enjoy.Struct;

import Annotation.StructIndex;
import Struct.BinaryType;
import Struct.StructBase;

/**
 * Created by 王彦鹏 on 2018-03-14.
 */

public class StructDemo extends StructBase {
    @StructIndex(0)
    private BinaryType ver = new BinaryType(1,byte.class);//   版本	1
    @StructIndex(1)
    private BinaryType data = new BinaryType(4,int.class);//    数据 4

    public BinaryType getVer() {
        return ver;
    }

    public void setVer(BinaryType ver) {
        this.ver = ver;
    }

    public BinaryType getData() {
        return data;
    }

    public void setData(BinaryType data) {
        this.data = data;
    }
}
