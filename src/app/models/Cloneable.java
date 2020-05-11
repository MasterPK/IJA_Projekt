package app.models;

public interface Cloneable {
    /**
     * Clone object without reference.
     * @return New object.
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;
}
