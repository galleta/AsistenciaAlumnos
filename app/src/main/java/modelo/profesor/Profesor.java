package modelo.profesor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by francis on 28/12/17.
 */

public class Profesor implements Serializable
{
    private String nombre, email, tipo, asignaturasimpartidas;
    private ArrayList<String> asignaturas1smr, asignaturas2smr, asignaturas1dam, asignaturas2dam;

    public Profesor(String nombre, String email, String tipo, String asignaturasimpartidas) {
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
        this.asignaturasimpartidas = asignaturasimpartidas;
        asignaturas1smr = new ArrayList<>();
        asignaturas2smr = new ArrayList<>();
        asignaturas1dam = new ArrayList<>();
        asignaturas2dam = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAsignaturasImpartidas() {
        return asignaturasimpartidas;
    }

    public void setAsignaturas1SMR(String asignaturas)
    {
        String[] separated1 = asignaturas.split(": ");

        if( separated1.length == 2 )
        {
            String[] separated2 = separated1[1].split(",");

            for(int i = 0; i < separated2.length; i++)
            {
                asignaturas1smr.add(separated2[i].trim());
            }
        }
    }

    public void setAsignaturas2SMR(String asignaturas)
    {
        String[] separated1 = asignaturas.split(": ");

        if( separated1.length == 2 )
        {
            String[] separated2 = separated1[1].split(",");

            for(int i = 0; i < separated2.length; i++)
            {
                asignaturas2smr.add(separated2[i].trim());
            }
        }
    }

    public void setAsignaturas1DAM(String asignaturas)
    {
        String[] separated1 = asignaturas.split(": ");

        if( separated1.length == 2 )
        {
            String[] separated2 = separated1[1].split(",");

            for(int i = 0; i < separated2.length; i++)
            {
                asignaturas1dam.add(separated2[i].trim());
            }
        }
    }

    public void setAsignaturas2DAM(String asignaturas)
    {
        String[] separated1 = asignaturas.split(": ");

        if( separated1.length == 2 )
        {
            String[] separated2 = separated1[1].split(",");

            for(int i = 0; i < separated2.length; i++)
            {
                asignaturas2dam.add(separated2[i].trim());
            }
        }
    }

    public void setAsignaturasImpartidas(String asignaturasimpartidas) {
        this.asignaturasimpartidas = asignaturasimpartidas;
    }

    public ArrayList<String> getAsignaturas1smr() {
        return asignaturas1smr;
    }

    public ArrayList<String> getAsignaturas2smr() {
        return asignaturas2smr;
    }

    public ArrayList<String> getAsignaturas1dam() {
        return asignaturas1dam;
    }

    public ArrayList<String> getAsignaturas2dam() {
        return asignaturas2dam;
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", tipo='" + tipo + '\'' +
                ", asignaturasimpartidas='" + asignaturasimpartidas + '\'' +
                '}';
    }
}
