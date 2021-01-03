
package opensource.poems.database;

import android.net.Uri;

public class PoemStore {

    public static final String AUTHORITY = "opensource.poems.database";

    public static final String CONTENT_AUTHORITY_SLASH = "content://" + AUTHORITY + "/";

    public static final String TABLAE_NAME = "poem";

    public static final String DB_NAME = "poem.db";

    public static Uri getContentUri() {
        return Uri.parse(CONTENT_AUTHORITY_SLASH + TABLAE_NAME);
    }

    public static final Uri getContentUri(String name) {
        return Uri.parse(CONTENT_AUTHORITY_SLASH + TABLAE_NAME + "/" + name);
    }

    public interface Columns {
        public static final String _ID = "_id";

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String POET = "poet";

        public static final String TYPE = "type";

        public static final String VALUE = "value";

        public static final String REMARK = "remark";

        public static final String TRANSLATION = "translation";

        public static final String ANALYSIS = "analysis";
    }

}
