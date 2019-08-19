package com.seekting.model.wenzikong

import com.google.gson.annotations.SerializedName

data class TabsItem(

	@field:SerializedName("models")
	val models: List<ModelsItem?>? = null,

	@field:SerializedName("name")
	val name: String? = null
)