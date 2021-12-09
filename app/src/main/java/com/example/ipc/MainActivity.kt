package com.example.ipc

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // 접속한 서비스 객체
    var ipcService: TestService? = null
    val button: Button by lazy { findViewById(R.id.button) }
    val textView: TextView by lazy { findViewById(R.id.textView) }

    // 서비스 접속을 관리하는 객체를 생성
    val connection = object : ServiceConnection {
        // 서비스에 접속이 성공 했을 떄 호출
        // 두번째 : 써비스의 onBind 매서드가 반환하는 객체를 받는다.
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 서비스를 추출
            val binder = service as TestService.LocalBinder
            ipcService = binder.getService()

        }

        // 서비스 접속을 해제 했을 떄 호출
        override fun onServiceDisconnected(name: ComponentName?) {
            ipcService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 서비스가 가동중이 아니라면 서비스를 가동한다.
        val chk = isServiceRunning("com.example.ipc.TestService")
        val serviceIntent = Intent(this, TestService::class.java)
        if (chk == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }

        // 서비스에 접속한다
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        button.setOnClickListener {
            var value = ipcService?.getNumber()
            textView.text = "value : $value"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 접속한 서비스의 접속을 해제한다.
        unbindService(connection)
    }

    // 가동중이 아닐 떄만 서비스를 가동
    //
    //@SuppressLint("ServiceCast")
    fun isServiceRunning(name: String): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // 현재 실행 중인 서비스 들을 가져옴
        val serviceList =
            manager.getRunningServices(Int.MAX_VALUE) // getRunningServices 모든 서비스를 다 가져올 수 있음(보안 이슈로 사용중지됨)(그런데 잘 동작함)
        // (현재 앱에서 동작시킨 서비스 목록만 가져옴)

        for (serviceInfo in serviceList) {
            // 서비스의 이름이 원하는 이름인가..
            if (serviceInfo.service.className == name) {
                return true
            }
        }
        return false
    }
}