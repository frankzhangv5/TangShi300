
package opensource.poems.database;

public class PoemBean {

    public long id;

    public String name;

    public String poet;

    public String type;

    public String value;

    public String remark;

    public String translation;

    public String analysis;

    public PoemBean(long id, String name, String poet, String type, String value, String remark, String translation,
            String analysis) {
        this.id = id;
        this.name = name;
        this.poet = poet;
        this.type = type;
        this.value = value;
        this.remark = remark;
        this.translation = translation;
        this.analysis = analysis;
    }

    public PoemBean() {

    }

    public void clear() {
        this.id = -1;
        this.name = null;
        this.poet = null;
        this.type = null;
        this.value = null;
        this.remark = null;
        this.translation = null;
        this.analysis = null;
    }

}
