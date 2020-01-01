package com.example.myapplication


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Gallery
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.fragment_image.*
import java.io.File
import java.io.FileOutputStream


class ImagesHolder {
    var image_holder: ArrayList<ImageItem> = ArrayList()

    fun getDataList() : ArrayList<ImageItem> {
        Log.wtf("ImagesHolder","getDataList -> size : ${image_holder.size}")
        return image_holder
    }

    fun setDataList(setlist : ArrayList<ImageItem>) {
        Log.wtf("ImagesHolder","setDataList -> size : ${image_holder.size}")
        image_holder = setlist
    }

    fun getDataById(position: Int) : ImageItem {
        return image_holder[position]
    }
}

var ImageHolder = ImagesHolder()

var images_list : ArrayList<ImageItem> = ArrayList()

var isfirst : Boolean = true

var initImgNum = 20
var totalImgNum = 20
var captureImgNum = 0
var newImgNum = 0
// i+c+n=t

class ImageFragment : Fragment(), ImageRecyclerAdapter.OnListItemSelectedInterface {

    lateinit var ImageRecyclerView : RecyclerView

    lateinit var rootView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 탭을 처음 실행했을 때만 디폴트 이미지 생성(추가)
        if (isfirst) {
            add_init()
            isfirst = false
        }

        rootView = inflater.inflate(R.layout.fragment_image, container, false)

        // 이미지는 recycler view로 구현
        ImageRecyclerView = rootView.findViewById(R.id.recyclerView!!)as RecyclerView
        ImageRecyclerView.adapter = ImageRecyclerAdapter(requireContext(), this, images_list)

        // 업로드 버튼 - 클릭 시 사진 추가 가능
        var uploadButton : Button = rootView.findViewById(R.id.uploadbutton)
        uploadButton.setOnClickListener {
            Toast.makeText(getContext(), "select image in your gallery", Toast.LENGTH_SHORT).show()
            loadImage()
        }

        // PaintFragment에서 받아오기
        var bundle: Bundle? = getArguments()
        Log.wtf("???","bundle ok")
        if(bundle != null ) {
            Log.wtf("???","bundle is not null - image")
            var captureNum: Int = bundle.getInt("Capture")

            var path: String = Environment.getExternalStorageDirectory().absolutePath + "/PaintCapture/Capture"+ captureNum +".jpeg"
            Log.wtf("file path","$path")
            var bitmap: Bitmap? = BitmapFactory.decodeFile(path)

            if(bitmap!=null) {
                captureImgNum++
                images_list = ImageHolder.getDataList()
                images_list.add(ImageItem(bitmap, "Captured Image ${captureImgNum}"))
                Log.wtf("???", "add ok")
                ImageHolder.setDataList(images_list)

                refresh()

                Log.wtf("???????????????????","${ImageRecyclerView.adapter!!.itemCount}")
                Log.wtf("???", "notify ok")
                totalImgNum++
                Log.wtf("???","totalImgNum:${totalImgNum}")
            }
        }

        return rootView
    }

    fun refresh(){
        activity?.also{
            var viewAdapter = ImageRecyclerAdapter(requireContext(), this, images_list)
            Log.i("HELLO", "print size ${images_list.size}")
            ImageRecyclerView = it.findViewById<RecyclerView>(R.id.recyclerView).apply {
                setHasFixedSize(true)
                adapter = viewAdapter
            }
            activity?.findViewById<LinearLayout>(R.id.fragment_image)?.invalidate()
        }
    }

    override fun onItemSelected(view: View, position: Int){
        Toast.makeText(getContext(), "Clicked Image ${position+1}", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, ImageInformation::class.java)
        intent.putExtra("POS", position)
        startActivity(intent)
    }


    fun add_init(){

        for(i in 1..initImgNum){
            var imageStr = "image"
            var titleStr = "title"
            if(i < 10) {
                imageStr += "0"
                titleStr += "0"
            }
            imageStr += i
            titleStr += i

            var packName = getActivity()!!.getPackageName()
            var resID = getResources().getIdentifier(imageStr, "drawable", packName)
            var textID = getResources().getIdentifier(titleStr,"string",packName)
            var resBitmap = BitmapFactory.decodeResource(getResources(), resID)

            images_list.add(ImageItem(resBitmap, getString(textID)))
        }
        ImageHolder.setDataList(images_list)
    }


    val Gallery = 0

    fun loadImage(){

        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), Gallery)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.wtf("???","onActivityResult call")
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Gallery){
            Log.wtf("???","request gallery")
            if(resultCode == RESULT_OK){

                Log.wtf("???","result ok")
                var dataUri : Uri? = data?.data

                // 갤러리에서 사진 불러오기
                try{
                    var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(getActivity()!!.getContentResolver(), dataUri)

                    newImgNum++
                    var titleStr = "new Image "+ newImgNum

                    images_list = ImageHolder.getDataList()
                    images_list.add(ImageItem(bitmap, titleStr))
                    ImageHolder.setDataList(images_list)

                    // 리스트에 추가한 후 recycler view에 반영 (맨 뒤에 추가함)
                    ImageRecyclerView.adapter!!.notifyDataSetChanged()
                    totalImgNum++

                    Toast.makeText(getContext(), "upload success", Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    Toast.makeText(getContext(), "$e", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }


}