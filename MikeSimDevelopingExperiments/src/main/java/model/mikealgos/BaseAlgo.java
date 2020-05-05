package main.java.model.mikealgos;

public abstract class BaseAlgo implements MikeAlgo {
    public enum status{
        CREATED,
        RUNNING,
        STOPPED
    }
}
