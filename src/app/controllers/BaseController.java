package app.controllers;

/**
 * GUI controllers must extends this class, because parent class can set GUI appearance through this functions.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
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
