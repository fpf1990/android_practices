package com.example.activitydemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.KeyEventDispatcher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static String TAG="MainActivity";

    //值必须大于0，-1的话默认会直接调用startActivity方法，startActivityForResult就失效了
    private static int LOGIN_REQUEST_CODE=1;

    private static final String REC_USERNAME="UserName";
    private static final String REC_PWD="password";

    EditText txt_UserName;
    EditText txt_pwd;
    Button btn_Commit;
    Button btn_Commit_Hide;
    private SharedPreferences mSharedPreferences;

    /*
    *activity生命周期阶段成对
    * eg：打开app过程 onCreate onStart onResume
    *     退出app过程 onPause  onStop  onDestory
    * eg: 系统返回键过程 onPause  onStop  onDestory       再次打开  onCreate onStart onResume
    *     系统HOME键过程 onPause  onStop                  再次打开  onStart onResume
    *     打开SecondActivity对应页面过程 onPause          返回MainActivity onResume
    *     横竖屏切换过程 onPause  onStop  onDestory  onCreate onStart onResume
    *     横竖屏切换会带来页面的重新创建刷新，会导致原本的状态消失，比如播放音乐进度一切换直接回零了
    *     避免这种情况：
    *     1.禁止旋转，指定屏幕方向(manifest文件中activity标签增加 android:screenOrientation="landscape"  portrait竖屏  landscape横屏)，游戏中常见
    *     2.对配置不敏感（manifest文件中activity标签增加 android:configChanges="keyboardHidden|screenSize|orientation" 这三个引起的变化就不会导致生命周期变化），视频播放常见
    *
    * 任务栈：
    *     manifest文件中activity标签增加android:launchMode="singleInstance"
    *     singleInstance   后面三种启动模式都是在同一个任务栈里的，singleInstance是独立的任务栈。他是一个单一的对象，独占一个栈，不会再创建，只会把他们提前
    *     说明：eg:activity1和activity2不停生成就会有Stack1和stack2两个栈分别存放这两种对象元素，当前再activity2页面时，stack2就排在stack1前面
    *     如果这时按返回键，则先将stack2全部出栈，再将stack1全部出栈
    *     应用场景：在整个系统中只有唯一一个实例，eg有道词典的取词，因为他在每个界面都可以取词
    *     singleTask       如果要创建的任务（activity）栈中没有，就会创建任务放置栈顶；如果任务在栈中已经存在，就把这个任务以上的任务全部出栈，是当前任务置于栈顶   使用场景：这个任务占的资源较大时
    *     singleTop        如果当前栈顶已经是这个任务（activity），就不会创建新的这个任务，注意是栈顶  比如浏览器书签，应用通知推送
    *     standard         创建新的任务，并且置于当前的栈顶，当我们点击返回时会销毁任务（activity）
     */

    /**
     * 生命周期-创建
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: *******************************************");
        initView();
        initListener();
        //获取返回之前保存的数据并加载到页面
        mSharedPreferences=this.getSharedPreferences(REC_USERNAME,MODE_PRIVATE);
        String rec_userName=mSharedPreferences.getString(REC_USERNAME,null);
        if(!TextUtils.isEmpty(rec_userName)){
            txt_UserName.setText(rec_userName);
        }
        String rec_pwd=mSharedPreferences.getString(REC_PWD,null);
        if(!TextUtils.isEmpty(rec_pwd)){
            txt_pwd.setText(rec_pwd);
        }
    }

    /**
     * 生命周期-开始（已经可见，但没有获取焦点，不可操作）
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: *******************************************");
    }

    /**
     * 生命周期-恢复（可见，获取焦点，可操作）
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: *******************************************");
    }

    /**
     * 生命周期-暂停（可见，失去焦点，不可操作）
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: *******************************************");
    }

    /**
     * 生命周期-停止（不可见，没有焦点，不可操作）
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: *******************************************");
    }

    /**
     * 生命周期-销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: *******************************************");
        //输入框中输入了内容，不小心按了系统的返回按钮，再次进来时输入的内容没了，导致用户体验差
        //可以在activity销毁之前将数据保存，再次创建时直接加载进来
        //获取要保存的数据并保存下来
        SharedPreferences.Editor edit=mSharedPreferences.edit();
        String userName=txt_UserName.getText().toString();
        if(!TextUtils.isEmpty(userName)){
            edit.putString(REC_USERNAME,userName);
        }
        String pwd=txt_pwd.getText().toString();
        if(!TextUtils.isEmpty(userName)){
            edit.putString(REC_PWD,pwd);
        }
        edit.commit();

    }


    /**
    * activity返回结果会在这里回调
    *
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGIN_REQUEST_CODE){
            if(resultCode==2){
                String msg="登录成功：" + data.getStringExtra("msg");
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==3){
                String msg="登录失败：" + data.getStringExtra("msg");
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView(){
        txt_UserName=findViewById(R.id.txtUserName);
        txt_pwd=findViewById(R.id.txtPwd);
        btn_Commit=findViewById(R.id.btnCommit);
        btn_Commit_Hide=findViewById(R.id.btnCommit2);
    }

    private void initListener(){
        btn_Commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ***************************************");
                HandleLogin(false);
            }
        });
        btn_Commit_Hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick_Hide: ***************************************");
                HandleLogin(true);
            }
        });
    }

    private void HandleLogin(boolean isHide){
        String userName=txt_UserName.getText().toString();
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this,R.string.tip3,Toast.LENGTH_SHORT).show();
            return;
        }
        String pwd=txt_pwd.getText().toString();
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this,R.string.tip4,Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isHide){
            //显示意图启动第二个页面
            //显示意图一般用于应用内部组件之间的跳转
            //第一种写法
            //Intent intent =new Intent(this,SecondActivity.class);
            //第二种写法
            Intent intent=new Intent();
            //获取包名
            String packageName=this.getPackageName();
            //获取类名
            String className=SecondActivity.class.getName();
            intent.setClassName(packageName,className);
//            intent.putExtra("userName",userName);
//            intent.putExtra("pwd",pwd);
            //传递类对象,对象要实现序列化接口（Serializeable或Parcelable）
            User user=new User(userName,pwd);
            intent.putExtra("user",user);
            //该方法无返回结果
//            startActivity(intent);
            //该方法有返回结果
            startActivityForResult(intent,LOGIN_REQUEST_CODE);
        }
        else{
            //隐式意图启动第三个页面
            //隐式意图一般用于应用之间的跳转
            Intent intent=new Intent();
            intent.setAction("com.example.activitydemo.LOGIN_INFO");
            //intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("userName",userName);
            intent.putExtra("pwd",pwd);
            startActivity(intent);

//            //假如想启动浏览器(如果是模拟器运行可以在adb命令行中先去抓取浏览器的包名和类名)
//            //第一种写法
//            intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
//            //第二种写法
//            ComponentName cm=new ComponentName("com.android.broser","com.android.broser.BrowserActivity");
//            intent.setComponent(cm);
//            startActivity(intent);
        }
    }

}