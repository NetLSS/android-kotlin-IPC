package com.example.ipc

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 서비스가 가동중이 아니라면 서비스를 가동한다.
        val chk = isServiceRunning("com.example.ipc.TestService")
        if (chk == false) {
            val serviceIntent = Intent(this, TestService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    // 가동중이 아닐 떄만 서비스를 가동
    //
    //@SuppressLint("ServiceCast")
    @SuppressLint("ServiceCast")
    fun isServiceRunning(name: String): Boolean {
        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as ActivityManager
        // 현재 실행 중인 서비스 들을 가져옴
        val serviceList = manager.getRunningServices(Int.MAX_VALUE) // getRunningServices 모든 서비스를 다 가져올 수 있음(보안 이슈로 사용중지됨)(그런데 잘 동작함)
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