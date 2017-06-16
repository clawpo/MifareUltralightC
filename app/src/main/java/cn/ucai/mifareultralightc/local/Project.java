package cn.ucai.mifareultralightc.local;

/**
 * Created by clawpo on 2017/6/16.
 */

public class Project {
    int id;
    String name;
    int type;
    String time;

    public Project(String name) {
        this.name = name;
        this.setType(0);
        this.setTime(String.valueOf(System.currentTimeMillis()));
    }

    public Project(String name, int type) {
        this.name = name;
        this.type = type;
        this.setTime(String.valueOf(System.currentTimeMillis()));
    }

    public Project(int id, String name, int type, String time) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", time='" + time + '\'' +
                '}';
    }
}
