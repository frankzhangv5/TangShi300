
package opensource.poems.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "Poems";

    private static final boolean DBG = true;

    private String mClassName;

    public Logger(Class<?> cls) {
        mClassName = cls.getSimpleName();
    }

    public void d(String msg) {
        if (DBG) {
            Log.d(TAG, mClassName + "\t" + msg);
        }
    }

    public void i(String msg) {
        Log.i(TAG, mClassName + "\t" + msg);
    }

    public void e(String msg) {
        Log.e(TAG, mClassName + "\t" + msg);
    }
}
