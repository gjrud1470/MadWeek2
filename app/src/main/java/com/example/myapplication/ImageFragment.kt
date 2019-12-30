package com.example.myapplication


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Gallery
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.fragment_image.*


class ImagesHolder {
    var image_holder: ArrayList<ImageItem> = ArrayList()

    fun getDataList() : ArrayList<ImageItem> {
        return image_holder
    }

    fun setDataList(setlist : ArrayList<ImageItem>) {
        image_holder = setlist
    }

    fun getDataById(position: Int) : ImageItem {
        return image_holder[position]
    }
}

var ImageHolder = ImagesHolder()


class ImageFragment : Fragment(), ImageRecyclerAdapter.OnListItemSelectedInterface {

    val images_list : ArrayList<ImageItem> = ArrayList()
    lateinit var ImageRecyclerView : RecyclerView

    var isfirst : Boolean = true

    lateinit var rootView : View

    var initImgNum = 13
    var totalImgNum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 탭을 처음 실행했을 때만 디폴트 이미지 생성(추가)
        if (isfirst) {
            Log.wtf("HELLO", "First init start")
            add_init()
            Log.wtf("HELLO", "First init done")
            totalImgNum = initImgNum
            isfirst = false
        }

        rootView = inflater.inflate(R.layout.fragment_image, container, false)

        // 업로드 버튼 - 클릭 시 사진 추가 가능
        var uploadButton : Button = rootView.findViewById(R.id.uploadbutton)
        uploadButton.setOnClickListener {
            Toast.makeText(getContext(), "select image in your gallery", Toast.LENGTH_SHORT).show()
            loadImage()
        }

        // 이미지는 recycler view로 구현
        ImageRecyclerView = rootView.findViewById(R.id.recyclerView!!)as RecyclerView
        ImageRecyclerView.adapter = ImageRecyclerAdapter(requireContext(), this, images_list)

        return rootView
    }

    override fun onItemSelected(view: View, position: Int){
        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show()
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
            ImageHolder.setDataList(images_list)
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

                // 갤러리에서 사진 불러오기
                try{
                    var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(getActivity()!!.getContentResolver(), dataUri)

                    var v = rootView.findViewById(R.id.recyclerView!!) as RecyclerView
                    var siz = v.getWidth()/3

                    bitmap = Bitmap.createScaledBitmap(bitmap, siz, siz, true)

                    var titleStr = "new Image "+(totalImgNum+1-initImgNum)

                    images_list.add(ImageItem(bitmap, titleStr))
                    ImageHolder.setDataList(images_list)

                    // 리스트에 추가한 후 recycler view에 반영 (맨 뒤에 추가함)
                    ImageRecyclerView.adapter!!.notifyItemInserted(totalImgNum)
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