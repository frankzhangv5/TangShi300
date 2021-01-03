
package opensource.poems;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import opensource.poems.database.PoemBean;
import opensource.poems.database.PoemStore;
import opensource.poems.database.PoemStore.Columns;
import opensource.poems.utils.Logger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.CursorLoader;
import android.app.LoaderManager;
import android.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PomesActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger LOGGER = new Logger(PomesActivity.class);

    ListView mPoemsListView;

    SimpleCursorAdapter mAdapter;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            PoemBean poemBean = getPoemBeanByName((String) msg.obj);
            if (null == poemBean) {
                LOGGER.e("Not Found records in database .");
                return;
            }

            showPoemDetailsDlg(poemBean);
            // overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poems_list_layout);

        LOGGER.i(String.format("Module:%s, Author:%s, Publish Date:%s, Revision:%s", R.string.tangshi,
                "frankzhang2010@foxmail.com", "2015-08-22", "v1.0.1"));

        initViews();
        mContentResolver = getContentResolver();

        getLoaderManager().initLoader(0, null, this);

        PushAgent.getInstance(getApplicationContext()).onAppStart();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        String deviceToken = UmengRegistrar.getRegistrationId(getApplicationContext());
        LOGGER.d("token:" + deviceToken);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    // ----------------------------------------------------------
    private void initViews() {
        mPoemsListView = (ListView) findViewById(R.id.lv_poems);
        mAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.poems_list_item_layout, null,
                new String[] {
                        Columns._ID, Columns.NAME, Columns.POET,
                }, new int[] {
                        R.id.tv_item_id, R.id.tv_item_name, R.id.tv_item_author,
                }, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mPoemsListView.setAdapter(mAdapter);

        mPoemsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LOGGER.d("position:" + position);
                TextView poemNameTv = (TextView) view.findViewById(R.id.tv_item_name);
                if (null != poemNameTv) {
                    Message msg = mHandler.obtainMessage(0, poemNameTv.getText());
                    msg.sendToTarget();
                }
            }
        });
    }

    // ----------------------------------------------------------
    // LoaderCallbacks overrides
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), PoemStore.getContentUri(), new String[] {
                Columns._ID, Columns.NAME, Columns.POET,
        }, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    // ----------------------------------------------------------
    private ContentResolver mContentResolver;

    private PoemBean mPoemBean = new PoemBean();

    private PoemBean getPoemBeanByName(String poemName) {
        Uri uri = PoemStore.getContentUri(poemName);
        Cursor c = mContentResolver.query(uri, null, null, null, null);
        if (null != c && c.getCount() > 0) {
            mPoemBean.clear();
            c.moveToFirst();
            mPoemBean.id = c.getLong(c.getColumnIndex(Columns._ID));
            mPoemBean.name = trimNewLine(c.getString(c.getColumnIndex(Columns.NAME)));
            mPoemBean.poet = trimNewLine(c.getString(c.getColumnIndex(Columns.POET)));
            mPoemBean.value = trimNewLine(c.getString(c.getColumnIndex(Columns.VALUE)));
            mPoemBean.remark = trimNewLine(c.getString(c.getColumnIndex(Columns.REMARK)));
            mPoemBean.translation = trimNewLine(c.getString(c.getColumnIndex(Columns.TRANSLATION)));
            mPoemBean.analysis = trimNewLine(c.getString(c.getColumnIndex(Columns.ANALYSIS)));
            c.close();
            return mPoemBean;
        }
        return null;
    }

    private String trimNewLine(String source) {
        if (TextUtils.isEmpty(source)) {
            return "bad source string";
        }

        if ('\n' == source.charAt(0)) {
            return source.substring(1);
        }

        return source;
    }

    private void showPoemDetailsDlg(PoemBean poemBean) {
        if (null == poemBean) {
            LOGGER.d("Poem bean is null.");
            return;
        }

        View view = getLayoutInflater().inflate(R.layout.poems_item_detail_layout,
                (ViewGroup) findViewById(R.id.ll_poem_detial));
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.ll_poem_detial);
        TextView topTitleTv = (TextView) view.findViewById(R.id.tv_poems_top);
        TextView poemNameTv = (TextView) view.findViewById(R.id.tv_poem_name);
        TextView poetTv = (TextView) view.findViewById(R.id.tv_poet);
        TextView poemContentTv = (TextView) view.findViewById(R.id.tv_poem_content);
        TextView remarkContentTv = (TextView) view.findViewById(R.id.tv_remark);
        TextView remarkTv = (TextView) view.findViewById(R.id.tv_remark_content);
        TextView translationContentTv = (TextView) view.findViewById(R.id.tv_translation);
        TextView translationTv = (TextView) view.findViewById(R.id.tv_translation_content);
        TextView analysisContentTv = (TextView) view.findViewById(R.id.tv_analysis);
        TextView analysisTv = (TextView) view.findViewById(R.id.tv_analysis_content);

        topTitleTv.setText(getResources().getString(R.string.poem_count, poemBean.id));
        poemNameTv.setText(poemBean.name);
        poetTv.setText(poemBean.poet);
        poemContentTv.setText(poemBean.value);
        remarkContentTv.setVisibility(View.VISIBLE);
        remarkTv.setText(poemBean.remark);
        translationContentTv.setVisibility(View.VISIBLE);
        translationTv.setText(poemBean.translation);
        analysisContentTv.setVisibility(View.VISIBLE);
        analysisTv.setText(poemBean.analysis);

        Dialog dialog = new Dialog(this, R.style.DlgTheme);
        dialog.setContentView(layout);
        dialog.show();
    }

}
