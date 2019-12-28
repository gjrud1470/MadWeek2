package com.example.myapplication


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Gallery
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.*

class ImageFragment : Fragment() {

    val image_list : ArrayList<ImageItem> = ArrayList()
    lateinit var ImageRecyclerView : RecyclerView

    var isfirst : Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 탭을 처음 실행했을 때만 디폴트 이미지 생성(추가)
        if (isfirst) {
            add_init()
            isfirst = false
        }

        var rootView = inflater.inflate(R.layout.fragment_image, container, false)

        // 업로드 버튼 - 클릭 시 사진 추가 가능
        var uploadButton : Button = rootView.findViewById(R.id.uploadbutton)
        uploadButton.setOnClickListener {
            Toast.makeText(getContext(), "upload image from gallery", Toast.LENGTH_SHORT).show()

            loadImage()

        }

        // 이미지는 recycler view로 구현
        ImageRecyclerView = rootView.findViewById(R.id.recyclerView!!)as RecyclerView
        ImageRecyclerView.adapter = ImageRecyclerAdapter(image_list)

        return rootView
    }

    fun add_init(){

        var initNum = 10

        for(i in 1..initNum){
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
            val resBitmap = BitmapFactory.decodeResource(getResources(), resID)

            image_list.add(ImageItem(resBitmap, getString(textID)))
        }
    }


    val Gallery = 0

    fun loadImage(){

        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), Gallery)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Gallery){
            if(resultCode == RESULT_OK){
                var dataUri : Uri? = data?.data
                try{
                    val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(getActivity().contentResolver, dataUri)

                }catch (e:Exception){
                    Toast.makeText(getContext(), "$e", Toast.LENGTH_SHORT).show()
                }
            }
            else{

            }
        }


    }

}