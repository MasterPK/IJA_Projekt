package app.controllers;

/**
 * GUI controllers must extends this class, because main class sets GUI appearance through this functions.
 */
public abstract class BaseController {

    /**
     * Function that is called on Scene start up.
     */
    public abstract void startUp();

    /**
     * Function that is called on Scene close.
     */
    public abstract void close();


}
