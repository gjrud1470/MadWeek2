package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Half.toFloat
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.myapplication.canvasView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_paint.view.*
import java.lang.Exception


class Point() {
    var x: Float = 0f
    var y: Float = 0f
    var chk: Boolean = false
    var color: Int = 0

    constructor(x: Float, y: Float, chk: Boolean, color: Int) : this() {
        this.x = x
        this.y = y
        this.chk = chk
        this.color = color
    }
}

var points: ArrayList<Point> = ArrayList<Point>()
var nowColor: Int = Color.BLACK
var isfirst: Boolean = true

class canvasView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    override fun onDraw(canvas: Canvas){
        var p: Paint = Paint()
        p.setStrokeWidth(15f)
        for(i in 0..points.size-1){
            p.setColor(points.get(i).color)
            if(!points.get(i).chk) continue
            canvas.drawLine(points.get(i-1).x,points.get(i-1).y,points.get(i).x,points.get(i).y,p)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean{
        var x: Float = event.getX()
        var y: Float = event.getY()

        when(event.getAction()){
            MotionEvent.ACTION_DOWN -> {
                points.add(Point(x,y,false, nowColor))
                points.add(Point(x,y,true, nowColor))
            }
            MotionEvent.ACTION_MOVE -> {
                points.add(Point(x,y,true, nowColor))
            }
            MotionEvent.ACTION_UP -> {}
            else -> {}
        }
        invalidate()

        return true
    }
}

class PaintFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View?    {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_paint, container, false)

        if(isfirst){
            //points.add(Point(0f,0f,false,nowColor))
            isfirst=false
        }

        var myCanvas: canvasView = rootView.findViewById(R.id.canvasView)
        /*myCanvas.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                //myCanvas.invalidate()
                Log.wtf("??","onTouch")
                return true
            }
        })*/

        var clearButton : Button = rootView.findViewById(R.id.ClearBtn)
        clearButton.setOnClickListener {
            Toast.makeText(getContext(), "clear the canvas", Toast.LENGTH_SHORT).show()
            points.clear()
            isfirst=true
            myCanvas.invalidate()
        }
        var redButton : Button = rootView.findViewById(R.id.RedBtn)
        redButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to red", Toast.LENGTH_SHORT).show()
            nowColor = Color.RED
        }
        var blueButton : Button = rootView.findViewById(R.id.BlueBtn)
        blueButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to blue", Toast.LENGTH_SHORT).show()
            nowColor = Color.BLUE
        }
        var greenButton : Button = rootView.findViewById(R.id.GreenBtn)
        greenButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to green", Toast.LENGTH_SHORT).show()
            nowColor = Color.GREEN
        }
        var blackButton : Button = rootView.findViewById(R.id.BlackBtn)
        blackButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to black", Toast.LENGTH_SHORT).show()
            nowColor = Color.BLACK
        }


        return rootView
    }


}
