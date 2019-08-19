package com.seekting.model.wenzikong

import com.google.gson.annotations.SerializedName

data class ModelsItem(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("thumb")
	val thumb: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)