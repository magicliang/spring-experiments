package com.magicliang.experiments.aspect.configurable;

/**
 * project name: spring-experiments
 * <p>
 * description:
 *
 * @author magicliang
 * <p>
 * date: 2020-04-18 20:49
 */
public class Dog {

    private int id;
    private String name;

    public Dog() {
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Dog)) return false;
        final Dog other = (Dog) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Dog;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "Dog(id=" + this.getId() + ", name=" + this.getName() + ")";
    }
}
