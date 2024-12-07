package com.example.mapareto;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Ubicacion extends AppCompatActivity {
    TextView vRegistros; // Vista para mostrar los datos
    Cursor c; // Cursor para manejar los resultados de la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion2);
        // Vincular la vista TextView
        vRegistros = findViewById(R.id.tvUbicacion);
        // Acceso a la base de datos
        SqlLocalizacion dbHelper = new SqlLocalizacion(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Consulta para obtener todas las ubicaciones
        c = db.rawQuery("SELECT * FROM ubicaciones", null);
        // Procesar los resultados y mostrarlos en el TextView
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0); // ID
                String calle = c.getString(1); // Dirección
                double latitud = c.getDouble(2); // Latitud
                double longitud = c.getDouble(3); // Longitud

                // Agregar los datos al TextView
                vRegistros.append(
                        "ID: " + id + "\n" +
                                "Dirección: " + calle + "\n" +
                                "Latitud: " + latitud + "\n" +
                                "Longitud: " + longitud + "\n\n"
                );
            } while (c.moveToNext());
        }
        // Cerrar la base de datos
        db.close();
    }
}