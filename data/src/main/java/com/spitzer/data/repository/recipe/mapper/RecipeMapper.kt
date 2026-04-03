package com.spitzer.data.repository.recipe.mapper

import com.spitzer.data.local.recipe.entities.FavoriteRecipeEntity
import com.spitzer.data.local.recipe.entities.RecipeEntity
import com.spitzer.data.remote.recipe.api.dto.RecipePageResponse
import com.spitzer.data.remote.recipe.api.dto.RecipeResponse
import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipePage
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import java.net.MalformedURLException
import java.net.URL

object RecipeMapper {

    fun mapFromFavoriteEntity(entity: FavoriteRecipeEntity): Recipe {
        return Recipe(
            id = entity.id,
            title = entity.title,
            image = parseUrl(entity.image),
            summary = entity.summary
        )
    }

    fun mapToFavoriteEntity(recipe: Recipe): FavoriteRecipeEntity {
        return FavoriteRecipeEntity(
            id = recipe.id,
            title = recipe.title,
            image = recipe.image?.toString(),
            summary = recipe.summary
        )
    }

    fun mapFromRecipePageResponse(response: RecipePageResponse): RecipePage {
        return RecipePage(
            list = response.results.map {
                mapFromRecipeResponse(it)
            }.toMutableList(),
            totalResults = response.totalResults
        )
    }

    fun mapFromRecipeResponse(response: RecipeResponse): Recipe {
        return Recipe(
            id = response.id,
            title = response.title,
            image = parseUrl(response.image),
            summary = response.summary
        )
    }

    fun mapFromStoredRecipes(recipeEntities: List<RecipeEntity>): List<Recipe> {
        return recipeEntities.map {
            mapFromStoredRecipe(it)
        }
    }

    fun mapFromStoredRecipe(recipeEntity: RecipeEntity): Recipe {
        return Recipe(
            id = recipeEntity.id,
            title = recipeEntity.title,
            image = parseUrl(recipeEntity.image),
            summary = recipeEntity.summary
        )
    }

    fun mapToStoredRecipes(recipes: List<Recipe?>): List<RecipeEntity> {
        return recipes.mapNotNull {
            mapToStoredRecipe(it)
        }
    }

    private fun mapToStoredRecipe(recipe: Recipe?): RecipeEntity? {
        if (recipe == null) return null
        return RecipeEntity(
            id = recipe.id,
            title = recipe.title,
            image = recipe.image?.toString() ?: "",
            summary = recipe.summary,
        )
    }

    fun mapSortCriteria(sortCriteria: RecipeSortCriteria): String? {
        return when (sortCriteria) {
            RecipeSortCriteria.RELEVANCE -> null
            RecipeSortCriteria.POPULARITY -> "popularity"
            RecipeSortCriteria.PREPARATION_TIME -> "time"
            RecipeSortCriteria.CALORIES -> "calories"
        }
    }

    fun mapSortOrder(recipeSortOrder: RecipeSortOrder): String = when (recipeSortOrder) {
        RecipeSortOrder.ASCENDING -> "asc"
        RecipeSortOrder.DESCENDING -> "desc"
    }


    private fun parseUrl(urlString: String?): URL? = try {
        URL(urlString)
    } catch (e: MalformedURLException) {
        null
    }
}
