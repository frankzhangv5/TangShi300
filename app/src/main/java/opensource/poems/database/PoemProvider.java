
package opensource.poems.database;

import opensource.poems.utils.Logger;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PoemProvider extends ContentProvider {

    private static final Logger LOGGER = new Logger(PoemProvider.class);
    PoemDatabaseHelper mHelper = null;

    private static final int POEMS = 1;

    private static final int POEMS_ID = 2;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(PoemStore.AUTHORITY, PoemStore.TABLAE_NAME, POEMS);
        URI_MATCHER.addURI(PoemStore.AUTHORITY, PoemStore.TABLAE_NAME + "/*", POEMS_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new PoemDatabaseHelper(getContext());
        mHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        LOGGER.d("uri:" + uri.toString());

        int match = URI_MATCHER.match(uri);
        LOGGER.d( "match:" + match);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (db == null)
            return null;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        if (uri.getQueryParameter("distinct") != null) {
            qb.setDistinct(true);
        }
        switch (match) {
            case POEMS:
                qb.setTables(PoemStore.TABLAE_NAME);
                break;

            case POEMS_ID:
                qb.setTables(PoemStore.TABLAE_NAME);
                qb.appendWhere("name=?");
                selectionArgs = new String[] {
                    uri.getPathSegments().get(1),
                };
                break;

            default:
                break;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long rowId = db.insert(PoemStore.TABLAE_NAME, null, values);
        LOGGER.d( "insertFile: values=" + values + " returned: " + rowId);
        return Uri.parse(PoemStore.CONTENT_AUTHORITY_SLASH + rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
