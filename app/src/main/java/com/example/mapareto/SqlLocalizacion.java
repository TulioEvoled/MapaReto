package com.example.mapareto;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLocalizacion extends SQLiteOpenHelper {
    // Sentencia SQL para crear la tabla de ubicaciones
    private static final String CREATE_TABLE_UBICACIONES =
            "CREATE TABLE ubicaciones (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "calle TEXT, " +
                    "latitud REAL, " +
                    "longitud REAL)";
    public SqlLocalizacion(Context context) {
        // Nombre de la base de datos: "Direcciones", versión: 1
        super(context, "Direcciones", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla cuando se crea la base de datos
        db.execSQL(CREATE_TABLE_UBICACIONES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tabla si ya existe (actualización de base de datos)
        db.execSQL("DROP TABLE IF EXISTS ubicaciones");
        onCreate(db);
    }
}

