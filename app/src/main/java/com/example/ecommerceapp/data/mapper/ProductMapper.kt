package com.example.ecommerceapp.data.mapper

import com.example.ecommerceapp.data.remote.dto.ProductDto
import com.example.ecommerceapp.domain.model.Product
import javax.inject.Inject

class ProductMapper @Inject constructor() {
    fun mapFromDto(dto: ProductDto):Product{
        return Product(
            name = dto.name?:"",
            image = dto.image?:"",
            price = dto.price?:"",
            description = dto.description?:"",
            model = dto.model?:"",
            brand = dto.brand?:"",
            id = dto.id?:"",
            createdAt = dto.createdAt?:""
        )
    }
}