package app.controllers;

/**
 * All controllers must extends this interface, because main class sets GUI appearance through this functions.
 */
public abstract class BaseController {

    /**
     * Function that is called on Scene start up.
     */
    abstract void startUp();
}
