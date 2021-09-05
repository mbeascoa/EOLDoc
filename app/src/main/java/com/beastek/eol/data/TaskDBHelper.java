package com.beastek.eol.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.beastek.eol.data.TaskContract.TaskEntry;
import com.beastek.eol.model.Task;

import java.util.Date;

public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_TITLE + " VARCHAR(128),"+
                    TaskEntry.COLUMN_NAME_DESCRIPTION + " TEXT,"+
                    TaskEntry.COLUMN_NAME_DATE + " DATETIME,"+
                    TaskEntry.COLUMN_NAME_DONE + " BOOLEAN" +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    // Database Information
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Task.db";
    private static TaskDBHelper taskDBHelper;

    private TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static TaskDBHelper getInstance(Context context){
        if(taskDBHelper == null){
            taskDBHelper =  new TaskDBHelper(context);
        }
        return taskDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public long insert(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        if(task.getDate() != null){
            values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, task.getDate().getTime());
        }
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DONE, task.isDone());

        // Insert the new row, returning the primary key value of the new row
        long id = db.insert( TaskContract.TaskEntry.TABLE_NAME, null, values);
        return id;
    }


    // ======== OBTENER TODAS LAS TAREAS REALIZADAS  =================

    public Cursor getAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] fields = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskContract.TaskEntry.COLUMN_NAME_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,          // La tabla a consultar
                fields,                                     // Las columnas a devolver
                null,                                        // Las columnas de la cláusula WHERE
                null,                                          // Los valores de la clausula WHERE
                null,                                             // no agrupamos las filas
                null,                                                   // no filtramos por grupos de filas
                TaskContract.TaskEntry.COLUMN_NAME_DATE+" ASC"           // El criterio de ordenación /sort order)
        );
        return cursor;
    }
    // ======== OBTENER LAS TAREAS REALIZADAS  VALOR COLUMN_NAME_DONE A UNO REALIZADAS DONE=================

    public Cursor getDone(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] fields = {
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.COLUMN_NAME_TITLE,
            TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
            TaskContract.TaskEntry.COLUMN_NAME_DATE,
            TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,                      // La tabla a consultar
            fields,                                                 // las columnas a devolver
            TaskEntry.COLUMN_NAME_DONE+" = '1'",                      // Las columnas de la clausula  WHERE
            null,                                                   // Los valores de la clausula WHERE
            null,                                                   // no agrupamos las fijas
            null,                                                   // no filtramos por grupos de filas
            TaskContract.TaskEntry.COLUMN_NAME_DATE+" ASC"          // El criterio de ordenacion (sort order)
        );
        return cursor;
    }

    // ========= OBTENER TAREA EN BASE A UN ID QUE NOS PASAN ==================

    public Task getTask(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] fields = {
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.COLUMN_NAME_TITLE,
            TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
            TaskContract.TaskEntry.COLUMN_NAME_DATE,
            TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,                        // La tabla a consultar
            fields,                                                   // Las columnas de la tabla a recuperar en la consulta
            TaskEntry._ID+" = "+id,                          // Las columnas de la cláusula WHERE
            null,                                         // Los valores de la clausula WHERE
            null,                                             // no agrupamos las filas
            null,                                              // no filtramos por grupos de filas
            null                                              // El criterio de ordenación
        );

        Task task = new Task();
        if(cursor != null && cursor.moveToFirst()){
            task.set_id( cursor.getInt( cursor.getColumnIndex( TaskEntry._ID )));
            task.setTitle( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_TITLE )) );
            task.setDescription( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DESCRIPTION )) );
            Long valueDate = cursor.getLong(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DATE ));
            if(valueDate != 0){
                task.setDate( new Date( valueDate ));
            }
            task.setDone( cursor.getInt(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DONE )) > 0);
        }
        return task;
    }

    // ======== OBTENER LAS TAREAS PENDIENTES  VALOR COLUMN_NAME_DONE A CERO=================

    public Cursor getPending(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] fields = {
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.COLUMN_NAME_TITLE,
            TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
            TaskContract.TaskEntry.COLUMN_NAME_DATE,
            TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,                                  // La tabla a consultar
            fields,                                                             // los valores de las columnas que devolverá
            TaskEntry.COLUMN_NAME_DONE+" = '0'",                       // Las columnas para la clausula WHERE
            null,                                                   // Los valores para la clausula  WHERE
            null,                                                      // no agrupamos las filas (rows)
            null,                                                       // no filtramos por grupos de filas
            TaskContract.TaskEntry.COLUMN_NAME_DATE+" ASC"             // El criterio de ordenación
        );
        return cursor;
    }

        // =========  BORRAMOS una tarea existente pasando un id  ============

    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        // Definimos la parte 'where' de la query.
        String selection = TaskContract.TaskEntry._ID + " = ?";
        // Especificamos los argumentoss in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };
        // Ejecutamos  la sentencia SQL.
        db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

        // ACTUALIZAREMOS una tarea en base a los datos task que nos pasen  ================

    public void update(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        // Nuevo valor para una columna
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        if(task.getDate() != null){
            values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, task.getDate().getTime());
        }
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DONE, task.isDone());

        // Qué fila de la tabla actualizaremos basándonos en el ID
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(task.get_id()) };

        db.update(TaskContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
