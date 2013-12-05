package app.Exceptions;

/**
 *
 * @author omar
 */
public class MoreThanOneExpertError extends Error{
    public MoreThanOneExpertError(String s){
        super(s + "\n Se encontro mas de un tipo de Expert activo, no se qué hacer! :( ");
    }
}
