package com.example.testchapter4

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.room.Room
import com.example.testchapter4.model.History
import java.lang.StringBuilder
import java.math.BigInteger
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    //TODO: 뷰 선언
    private val txt_calc: TextView by lazy {
        findViewById(R.id.txt_calc)
    }
    private val txt_result: TextView by lazy {
        findViewById(R.id.txt_result)
    }
    private val HistoryLayout: View by lazy {
        findViewById(R.id.HistoryLayout)
    }
    private val HistoryLinearLayout: LinearLayout by lazy {
        findViewById(R.id.HistoryLinearLayout)
    }

    //TODO: Database 선언
    lateinit private var db: AppDatabase

    //TODO: Operator인지, Operator가 사용되었는지 체크
    private var isOP: Boolean = false
    private var hasOP: Boolean = false

    //TODO: strBuilder 선언, 숫자 길이 제한 목적으로 생성
   private var strBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO: DB 생성 Result 버튼 누를떄 DB에 Insert
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()

        txt_calc.setText("0")

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_num0 -> numberButtonClick("0")
            R.id.btn_num1 -> numberButtonClick("1")
            R.id.btn_num2 -> numberButtonClick("2")
            R.id.btn_num3 -> numberButtonClick("3")
            R.id.btn_num4 -> numberButtonClick("4")
            R.id.btn_num5 -> numberButtonClick("5")
            R.id.btn_num6 -> numberButtonClick("6")
            R.id.btn_num7 -> numberButtonClick("7")
            R.id.btn_num8 -> numberButtonClick("8")
            R.id.btn_num9 -> numberButtonClick("9")


            R.id.btn_clear -> {
                txt_calc.setText("0")
                txt_result.setText("")
                isOP = false
                hasOP = false
                strBuilder.clear()
            }

            R.id.btn_result -> {
                //TODO: DB에 넣어주는 부분
                //TODO : DB와 관련된 부분은 메인쓰레드가 아닌 새로운 쓰레드에서 작업

                val values = txt_calc.text.split(" ")
                val num1 = values[0].toBigInteger()
                val num2 = values[2].toBigInteger()
                val op = values[1]

                val expression = txt_calc.text.toString()
                val result = txt_result.text.toString()

                //Todo: 계산기록 저장
                saveHistory(expression, result)

                txt_calc.setText(calculation(num1, num2, op).toString())
                txt_result.setText("")

                isOP = false
                hasOP = false

            }
        }
    }

    //TODO: 계산기록 버튼 누를 때
    fun ImageButtonClick(v: View) {
        when (v.id) {
            R.id.btn_history -> {
                HistoryLayout.isVisible = true
            }
        }
    }

    //TODO: operator 누를 경우
    fun operatorButtonClick(v: View) {
        when (v.id) {
            R.id.btn_add -> {
                operatorButtonClick("+")
            }
            R.id.btn_sub -> {
                operatorButtonClick("-")
            }
            R.id.btn_mul -> {
                operatorButtonClick("*")
            }
            R.id.btn_div -> {
                operatorButtonClick("/")
            }
            R.id.btn_percent -> {
                operatorButtonClick("%")
            }
        }
    }

    //TODO: 창닫기, 계산기록 삭제
    fun historyLayoutButtonClick(v: View) {
        when (v.id) {
            R.id.btn_close -> {
                HistoryLayout.isVisible = false
            }
            R.id.btn_historyDelete -> {
                //TODO: DB Data 삭제
                Thread(Runnable {
                    db.historyDao().deleteAll()
                })

                //TODO: View 삭제
                HistoryLinearLayout.removeAllViews()
            }
        }
    }

    /**
     *      숫자 키패드 누를 때
     */
    private fun numberButtonClick(buttonNumber: String) {


        //TODO: 연산자가 눌린 후에 숫자를 입력받을때 연산자 뒤에 빈칸 추가
        if (isOP && hasOP) {
            txt_calc.append(" ")
            strBuilder.clear()
            isOP = false
        }


        //TODO: 맨 처음 숫자를 입력받을 때 표시된 숫자가 0일때 예외처리
        if (txt_calc.text.toString().equals("0") && buttonNumber.equals("0")) {
            return@numberButtonClick
        } else if (txt_calc.text.toString().equals("0") && !buttonNumber.equals("0")) {
            txt_calc.text = buttonNumber
            strBuilder.append(buttonNumber)
        } else if(strBuilder.length < 8) {
            txt_calc.append(buttonNumber)
            strBuilder.append(buttonNumber)
        } else {
            Toast.makeText(this, "숫자는 8자리 이상 입력할 수 없습니다", Toast.LENGTH_SHORT).show()
        }


        //TODO: 빈칸을 기준으로 문자열을 나눴을 때 크기가 3개 이상(숫자,연산자,숫자)일 경우 처리
        if (txt_calc.text.split(" ").size >= 3) {
            val values = txt_calc.text.split(" ")
            val num1 = values[0].toBigInteger()
            val num2 = values[2].toBigInteger()
            val op = values[1]
            txt_result.setText(calculation(num1, num2, op).toString())
        }
    }


    //TODO: operator 누를 경우
    private fun operatorButtonClick(operator: String) {

        if (!isOP) {
            if (!hasOP) {
                txt_calc.append(" " + operator)
                isOP = true
                hasOP = true
            } else {
                Toast.makeText(this, "연산자는 한 번만 사용할 수 있습니다", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (hasOP) {
                val str = SpannableStringBuilder(txt_calc.text)
                str.delete(str.length - 1, str.length)
                txt_calc.setText(str)
                txt_calc.append(operator)
            }
        }

    }

    private fun calculation(num1: BigInteger, num2: BigInteger, op: String): BigInteger? {

        var rVal: BigInteger? = BigInteger.ZERO

        when (op) {
            "+" -> rVal = num1 + num2
            "-" -> rVal = num1 - num2
            "*" -> rVal = num1 * num2
            "/" -> {
                if (num1 != BigInteger.ZERO && num2 != BigInteger.ZERO)
                    rVal = num1 / num2
                else {
                    Toast.makeText(this, "0으로 나누기 연산을 할 수 없습니다", Toast.LENGTH_SHORT).show()
                    rVal = null
                }
            }
            "%" -> rVal = num1 % num2
        }
        return rVal
    }

    //TODO: Save History
    private fun saveHistory(expression: String, result: String) {

        Thread(Runnable {
            db.historyDao().insertHistory(
                History(
                    uid = null,
                    expression = expression,
                    result = result
                )
            )
        }).start()

        val view = layoutInflater.inflate(R.layout.history_addview_layout, null, false)
        val history_expression = view.findViewById<TextView>(R.id.history_expression)
        val history_result = view.findViewById<TextView>(R.id.history_result)

        history_expression.setText(expression)
        history_result.setText("= $result")

        HistoryLinearLayout.addView(view, 0)

    }
}