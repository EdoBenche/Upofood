package it.benche.upofood

import android.Manifest
import android.R.attr.name
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import it.benche.upofood.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.confirm_dialog.*
import kotlinx.android.synthetic.main.layout_categories.*
import kotlinx.android.synthetic.main.layout_categories.ivClose
import kotlinx.android.synthetic.main.layout_select_media.*
import java.util.*
import kotlin.properties.Delegates


class AddProductActivity: AppCompatActivity() {

    lateinit var imageProduct: Uri
    private val RESULT_LOAD_IMAGE = 100
    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private var type by Delegates.notNull<Int>()

    private lateinit var fileName: String

    private lateinit var dialog: Dialog
    private lateinit var dialog2: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        addImage()
        btnAddImage.onClick {
            dialog2.show()
        }

        btnAddProduct.onClick {
            addProductInCloud()
        }
        initCategoriesDialog()
        edtSpinnerCategories.onClick {
            dialog.show()
        }
    }

    private fun addImage() {
        dialog2 = BottomSheetDialog(this)
        dialog2.setContentView(R.layout.layout_select_media)
        dialog2.rgSelectMedia.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.fromGallery -> {
                    dialog2.dismiss()
                    type = 0
                    checkPermissionForImage()
                }
                R.id.fromCamera -> {
                    dialog2.dismiss()
                    type = 1
                    takeAPicture()
                }
            }
        }

        dialog2.ivClose.onClick {
            dialog2.dismiss()
        }



    }

    private fun chooseFromGallery() {
        val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(i, 1000)
    }


    private fun addProductInCloud() {
        val product = hashMapOf(
                "nomeProdotto" to edtProduct.text.toString(),
                "descrizione" to edtDescription.text.toString(),
                "prezzo" to edtPrice.text.toString(),
                "quantita" to edtQty.text.toString(),
                "categoria" to edtSpinnerCategories.text.toString(),
                "img" to fileName
        )

        // Add a new document with a generated ID
        val TAG: String = "AddProductActivity";
        var uid: String? = mAuth.uid
        if (uid != null) {
            db.collection("products")
                    .document(edtProduct.text.toString())
                    .set(product)
                    .addOnSuccessListener { Log.d(TAG, "Prodotto aggiunto correttamente")}
                    .addOnFailureListener(OnFailureListener { e -> Log.w(TAG, "Error adding document", e) })
        }
        confirmed()

    }

    private fun confirmed() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.confirm_dialog)
        dialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()

        Handler().postDelayed({
            startActivity(Intent(this, DrawerActivity::class.java))
        }, 4000)

    }

    private fun takeAPicture() {
        //if system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(permission, 1001) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(permissionCoarse, 1002) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                chooseFromGallery()
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageProduct = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageProduct)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)


    }

    private fun addImgInFirebaseStorage(fileUri: Uri) {
        fileName = UUID.randomUUID().toString() +".jpg"
        val image: StorageReference = FirebaseStorage.getInstance().reference.child("images/$fileName")
        image.putFile(fileUri).addOnSuccessListener {
            image.downloadUrl.addOnSuccessListener { uri -> Log.d("tag", "onSuccess: Uploaded Image URl is $uri") }
            Toast.makeText(this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { Toast.makeText(this, "Upload Failled.", Toast.LENGTH_SHORT).show() }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK && type == 1){
            //set image captured to image view
            addImgInFirebaseStorage(imageProduct)

            btnAddProduct.isEnabled = true
            btnAddProduct.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.md_orange_500))
            tvAddImage.text = "Immagine aggiunta"
            tvAddImage.setTextColor(Color.parseColor("#32CD32"))
            phCheck.setImageResource(R.drawable.ic_check_green)
            phCheck.setColorFilter(Color.parseColor(("#32CD32")))
            btnAddImage.isEnabled = false
        }

        if(resultCode == Activity.RESULT_OK && requestCode == 1000 && type == 0) {
            if (data != null) {
                imageProduct = data.data!!
            }
            addImgInFirebaseStorage(imageProduct)

            btnAddProduct.isEnabled = true
            btnAddProduct.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.md_orange_500))
            tvAddImage.text = "Immagine aggiunta"
            tvAddImage.setTextColor(Color.parseColor("#32CD32"))
            phCheck.setImageResource(R.drawable.ic_check_green)
            phCheck.setColorFilter(Color.parseColor(("#32CD32")))
            btnAddImage.isEnabled = false
        }
    }

    private fun initCategoriesDialog() {
        dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.layout_categories)
        dialog.rgCategories.setOnCheckedChangeListener{ _, checkedId ->
            when (checkedId) {
                R.id.bevande -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Bevande")
                }
                R.id.bimbi -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Articoli per il bambino")
                }
                R.id.carne -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Carne")
                }
                R.id.casa -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Tutto per la casa")
                }
                R.id.dolci -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Dolci")
                }
                R.id.frutta_e_verdura -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Frutta e verdura")
                }
                R.id.gastronomia_pasta_fresca -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Gastronomia e pasta fresca")
                }
                R.id.gelati_surgelati -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Gelati e surgelati")
                }
                R.id.igiene_personale -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Igiene personale")
                }
                R.id.latticini -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Latte, burro, uova e yogurt")
                }
                R.id.pane_e_pasticceria -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Pane e pasticceria")
                }
                R.id.pesce -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Pesce")
                }
                R.id.prodotti_alimentari -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Prodotti alimentari")
                }
                R.id.prodotti_animali -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Prodotti per animali")
                }
                R.id.ricorrenze -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Prodotti per festività")
                }
                R.id.salumi_e_formaggi -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Salumi e formaggi")
                }
                R.id.tempo_libero -> {
                    dialog.dismiss()
                    edtSpinnerCategories.setText("Tempo libero")
                }
            }
        }
        dialog.ivClose.onClick {
            dialog.dismiss()
        }
    }
}