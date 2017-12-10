package com.foolchen.multiitemtextview.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    mitv.postDelayed({
      mitv.setTexts("测试用文本1", "1234测试用长文本2", "测试用长文本333333333333333333333333", "测试用文本4")

    }, 1000)

    mitv.postDelayed({ mitv.setItemCount(4) }, 2000)
    mitv.postDelayed({ mitv.setTextSize(60F) }, 3000)
  }
}
