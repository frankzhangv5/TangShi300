
package opensource.poems.database;

import java.util.HashSet;
import java.util.List;

import opensource.poems.utils.Logger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;

public class PoemDatabaseHelper extends SQLiteOpenHelper {

    private static final Logger LOGGER = new Logger(PoemDatabaseHelper.class);

    private static final String DATABASE_NAME = PoemStore.DB_NAME;

    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    private static final HashSet<String> mValidTables = new HashSet<String>();

    static {
        mValidTables.add(PoemStore.TABLAE_NAME);
    }

    public static boolean isValidTable(String name) {
        return mValidTables.contains(name);
    }

    public PoemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    private void createPoetryTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE poem (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "id INTEGER,"
                + "name TEXT UNIQUE ON CONFLICT REPLACE," + "poet TEXT," + "type TEXT," + "value TEXT,"
                + "remark TEXT," + "translation TEXT," + "analysis TEXT" + ");");
        db.execSQL("CREATE INDEX poemIndex1 ON poem (name);");
    }

    private void loadPoetryTable(SQLiteDatabase db) {
        SQLiteStatement stmt = null;
        try {
            stmt = db
                    .compileStatement("INSERT OR IGNORE INTO poem(id,name,poet,type,value,remark,translation,analysis)"
                            + " VALUES(?,?,?,?,?,?,?,?);");

            List<PoemBean> poemBeans = null;
            try {
                poemBeans = PoemXmlParser.parse(mContext);
            } catch (Exception e) {
                LOGGER.e(e.getMessage());
                return;
            }

            if (null == poemBeans || poemBeans.size() < 1) {
                return;
            }

            for (PoemBean item : poemBeans) {
                loadPoetry(stmt, item.id, item.name, item.poet, item.type, item.value, item.remark, item.translation,
                        item.analysis);
            }

        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    private void loadPoetry(SQLiteStatement stmt, long id, String name, String poet, String type, String value,
            String remark, String translation, String analysis) {
        LOGGER.d("load poem: " + id + ", " + name + ", " + poet + ", " + type + ", " + value + ", " + remark);
        stmt.bindLong(1, id);
        stmt.bindString(2, name);
        stmt.bindString(3, poet);
        stmt.bindString(4, type);
        stmt.bindString(5, value);
        stmt.bindString(6, remark);
        stmt.bindString(7, translation);
        stmt.bindString(8, analysis);
        stmt.execute();
    }

    public String getStringValueFromTable(SQLiteDatabase db, String table, String name, String defaultValue) {
        Cursor c = null;
        try {
            c = db.query(table, new String[] {
                Settings.System.VALUE
            }, "name='" + name + "'", null, null, null, null);
            if (c != null && c.moveToFirst()) {
                String val = c.getString(0);
                return val == null ? defaultValue : val;
            }
        } finally {
            if (c != null)
                c.close();
        }
        return defaultValue;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOGGER.d("DatabaseHelper::onCreate");
        createPoetryTable(db);

        loadPoetryTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        LOGGER.d("Upgrading settings database from version " + oldVersion + " to " + currentVersion);

        // int upgradeVersion = oldVersion;
    }

}
