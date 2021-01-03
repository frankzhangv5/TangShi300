
package opensource.poems;

import opensource.poems.utils.Logger;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartActivity extends Activity {

    private static final Logger LOGGER = new Logger(StartActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        // 闪屏的核心代码
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, PomesActivity.class); // 从启动动画ui跳转到主ui
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                StartActivity.this.finish(); // 结束启动动画界面

            }
        }, 1500); // 启动动画持续3秒钟

        MobclickAgent.updateOnlineConfig(getApplicationContext());
        PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
        mPushAgent.enable();
        PushAgent.getInstance(getApplicationContext()).onAppStart();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
