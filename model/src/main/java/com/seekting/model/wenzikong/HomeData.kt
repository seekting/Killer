package com.seekting.model.wenzikong

import com.google.gson.annotations.SerializedName

data class HomeData(

	@field:SerializedName("tabs")
	val tabs: List<TabsItem?>? = null
)