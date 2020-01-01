package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.util.Half.toFloat
import android.util.Log
import android.util.Log.d
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.canvasView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_paint.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import com.example.myapplication.ImageFragment


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
var penSize: Float = 10f

class canvasView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    override fun onDraw(canvas: Canvas){
        var p: Paint = Paint()
        p.setStrokeWidth(penSize)
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

        var myCanvas: canvasView = rootView.findViewById(R.id.canvasView)


        // reset canvas
        var clearButton : Button = rootView.findViewById(R.id.ClearBtn)
        clearButton.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Will you CLEAR the canvas?")
            builder.setMessage("You will lose all of the data.")
            builder.setPositiveButton("YES"){ _, _ ->
                Toast.makeText(getContext(), "clear success", Toast.LENGTH_SHORT).show()
                points.clear()
                myCanvas.invalidate()
            }
            builder.setNegativeButton("NO"){ _, _ ->
                Toast.makeText(getContext(), "clear fail", Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }


        // capture canvas
        var captureNum: Int = 0

        var captureButton : Button = rootView.findViewById(R.id.CaptureBtn)
        captureButton.setOnClickListener {
            Toast.makeText(getContext(), "capture the canvas", Toast.LENGTH_SHORT).show()

            var captureView: View = rootView.findViewById(R.id.canvasView)

            var path: String = Environment.getExternalStorageDirectory().absolutePath + "/PaintCapture";
            Log.wtf("file path","$path")
            var file: File = File(path)

            if(!file.exists()){
                Log.wtf("file path","not exist -> mkdir")
                file.mkdirs()
                Log.wtf("file path","not exist -> mkdir")
            }

            var fos: FileOutputStream? = null
            try{
                captureNum++
                fos = FileOutputStream(path + "/Capture"+ captureNum +".jpeg")
                viewToBitmap(captureView).compress(Bitmap.CompressFormat.JPEG, 100, fos)
                getContext()!!.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))

                Toast.makeText(getContext(), "save the file", Toast.LENGTH_SHORT).show()
                fos.flush()
                fos.close()
                captureView.destroyDrawingCache()

                var bundle: Bundle = Bundle()
                bundle.putInt("Capture",captureNum)
                if(bundle!=null) {
                    Log.wtf("???", "bundle is not null - paint")
                    var fragment: ImageFragment = ImageFragment()
                    fragment.setArguments(bundle)
                    var fragmentTransaction: FragmentTransaction = getFragmentManager()!!.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fragment_image, fragment)
                    fragmentTransaction.commit()
                }

            }catch(e: FileNotFoundException){
                e.printStackTrace()
            }catch(e: IOException){
                e.printStackTrace()
            }

        }



        // pen color
        var redButton : Button = rootView.findViewById(R.id.RedBtn)
        redButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to Red", Toast.LENGTH_SHORT).show()
            nowColor = Color.RED
        }
        var yellowButton : Button = rootView.findViewById(R.id.YellowBtn)
        yellowButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to Yellow", Toast.LENGTH_SHORT).show()
            nowColor = Color.YELLOW
        }
        var greenButton : Button = rootView.findViewById(R.id.GreenBtn)
        greenButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to Green", Toast.LENGTH_SHORT).show()
            nowColor = Color.GREEN
        }
        var cyanButton : Button = rootView.findViewById(R.id.CyanBtn)
        cyanButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to Cyan", Toast.LENGTH_SHORT).show()
            nowColor = Color.CYAN
        }
        var blueButton : Button = rootView.findViewById(R.id.BlueBtn)
        blueButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to Blue", Toast.LENGTH_SHORT).show()
            nowColor = Color.BLUE
        }
        var magentaButton : Button = rootView.findViewById(R.id.MagentaBtn)
        magentaButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to Magenta", Toast.LENGTH_SHORT).show()
            nowColor = Color.MAGENTA
        }
        var blackButton : Button = rootView.findViewById(R.id.BlackBtn)
        blackButton.setOnClickListener {
            Toast.makeText(getContext(), "change color to Black", Toast.LENGTH_SHORT).show()
            nowColor = Color.BLACK
        }

        // eraser
        var eraserButton : Button = rootView.findViewById(R.id.EraserBtn)
        eraserButton.setOnClickListener {
            Toast.makeText(getContext(), "eraser", Toast.LENGTH_SHORT).show()
            nowColor = getResources().getColor(R.color.paintBackground)
        }

        // undo
        var undoButton : Button = rootView.findViewById(R.id.UndoBtn)
        undoButton.setOnClickListener {
            if(points.size==0){
                Toast.makeText(getContext(), "undo Fail (no data)", Toast.LENGTH_SHORT).show()
            }
            else {
                for (i in points.size - 1 downTo 0) {
                    if (points[i].chk) {
                        points.removeAt(i)
                    } else {
                        points.removeAt(i)
                        break
                    }
                    myCanvas.invalidate()
                }
                Toast.makeText(getContext(), "undo", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }


    fun viewToBitmap(view: View): Bitmap {
        var bitmap: Bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        var canvas: Canvas = Canvas(bitmap)
        view.layout(0,0, view.getWidth(), view.getHeight())

        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }




}
