package com.salim.salimzhulkhrni.student_marketplace

class ProductData(

    var address : String,
    var category : String,
    var description : String,
    var name : String,
    var price : Long,
    var url : String,
    var user_id : String,
    var favorites : Boolean,
    var key : String
)

{
    constructor():this("","","","",0,"","",false,"")
}
