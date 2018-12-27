package com.salim.salimzhulkhrni.student_marketplace

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.toolbar.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AddProductActivity : AppCompatActivity() {

    val TAG = "add_product_fragment"

    private var btn: Button? = null
    private var imageview: ImageView? = null
    private val GALLERY = 1
    private val CAMERA = 2

    lateinit var bitmap: Bitmap
    lateinit var imageViewForProduct: ImageView
    lateinit var nameOfProduct: EditText
    lateinit var priceOfProduct: EditText
    lateinit var addressOfProduct: EditText
    lateinit var categoryOfProduct: Spinner
    lateinit var descrptionOfProduct: EditText
    lateinit var buttonToAddProduct: Button

    var category = "Electronics"

    var contentURI : Uri? = null

    val storage = FirebaseStorage.getInstance()

    val user_id = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        setSupportActionBar(mainToolBar)

        val toolbar_ref = supportActionBar
        toolbar_ref!!.title = ""

        toolbar_title.text = "Add Product"

        toolbar_ref.setDisplayHomeAsUpEnabled(true)

        toolbar_ref.setDisplayShowTitleEnabled(false)

        mainToolBar.setNavigationOnClickListener {
            finish()
        }

        //Inizilize all variables
        initializeAllVariables();

        imageViewForProduct!!.setOnClickListener { showPictureDialog() }
        buttonToAddProduct!!.setOnClickListener { fetchDataAndAddProduct() }
    }

    fun initializeAllVariables(){

        //Function for all the variable's initialization including Textview and Buttons.

        imageViewForProduct = findViewById<View>(R.id.imageviewForProduct) as ImageView
        nameOfProduct = findViewById<View>(R.id.nameOfProduct) as EditText
        priceOfProduct = findViewById<View>(R.id.priceOfProduct) as EditText
        addressOfProduct = findViewById<View>(R.id.addressOfProduct) as EditText
        categoryOfProduct = findViewById<View>(R.id.categoryOfProduct) as Spinner
        descrptionOfProduct = findViewById<View>(R.id.descriptionOfProduct) as EditText
        buttonToAddProduct = findViewById<View>(R.id.buttonToAddProduct) as Button

        val myCategory = arrayOf("Electronics", "Home and Kitchen" , "Automobiles" , "Clothes and Footwear" , "Others")
        categoryOfProduct.adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, myCategory)
        categoryOfProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                category = myCategory[p2]
            }

        }
    }

    fun fetchDataAndAddProduct(){

        val name = nameOfProduct.text.toString()
        var price : Long = 0L
        val address = addressOfProduct.text.toString()
        val description = descrptionOfProduct.text.toString()

        /*if(contentURI == null){
            Toast.makeText(this,"Please insert an image",Toast.LENGTH_SHORT).show()
            return
        }*/
        if(name.isEmpty()){
            Toast.makeText(this,"Name of the product can't be Empty",Toast.LENGTH_SHORT).show()
            return
        }
        if(address.isEmpty()){
            Toast.makeText(this,"Address can't be Empty",Toast.LENGTH_SHORT).show()
            return
        }
        if(priceOfProduct.text.toString().isEmpty()){
            Toast.makeText(this,"Price of the product can't be Empty",Toast.LENGTH_SHORT).show()
            return
        }
        else{
            price = priceOfProduct.text.toString().toLong()
        }
        if(description.isEmpty()){
            Toast.makeText(this,"Description can't be Empty",Toast.LENGTH_SHORT).show()
            return
        }

        Log.i("Bitmap Information",bitmap.toString())

        var baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos)
        val data1 = baos!!.toByteArray()

        val path = "images/" + UUID.randomUUID() +".png"

        val firememeref = storage.getReference(path)

        val ref1 = storage.reference.child(path)

        //val metadata = StorageMetadata.Builder().setCustomMetadata("text",)
        val uploadTask = firememeref.putBytes(data1)

        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref1.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                //Log.i("URL Downloaded", downloadUri.toString())

                val data = Product(name,price, address, category, description, downloadUri.toString(),user_id,"yes")

                val ref = FirebaseDatabase.getInstance().getReference()
                val key = ref.child("Products").push().key
                ref.child("Products").child(key!!).setValue(data)

                val intent = Intent(this, ViewProductActivity::class.java)
                intent.putExtra("Key",key.toString())
                finish()
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)

            } else {

            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                contentURI = data!!.data
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this@AddProductActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageViewForProduct!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@AddProductActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            contentURI = data!!.data
            bitmap = data!!.extras!!.get("data") as Bitmap
            imageViewForProduct!!.setImageBitmap(bitmap)
            saveImage(bitmap)
            Toast.makeText(this@AddProductActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }
}
