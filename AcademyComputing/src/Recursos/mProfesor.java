/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Recursos;
/**
 *
 * @author GLARA
 */

public class mProfesor {

    private final String nombre;
    private final String id;
    

    public mProfesor(String nombre,String id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getID() {
        return id;
    }

        
    @Override
    public String toString() {
        return nombre;
    }

}