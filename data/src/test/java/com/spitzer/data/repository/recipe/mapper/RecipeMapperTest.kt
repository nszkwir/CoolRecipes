package com.spitzer.data.repository.recipe.mapper

import com.spitzer.data.local.recipe.entities.FavoriteRecipeEntity
import com.spitzer.data.local.recipe.entities.RecipeEntity
import com.spitzer.data.remote.recipe.api.dto.RecipePageResponse
import com.spitzer.data.remote.recipe.api.dto.RecipeResponse
import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class RecipeMapperTest {

    @Test
    fun `mapFromFavoriteEntity maps correctly with valid image`() {
        val entity = FavoriteRecipeEntity(
            id = 1L,
            title = "Pizza",
            image = "https://example.com/pizza.jpg",
            summary = "Delicious pizza"
        )

        val recipe = RecipeMapper.mapFromFavoriteEntity(entity)

        assertEquals(1L, recipe.id)
        assertEquals("Pizza", recipe.title)
        assertEquals(URL("https://example.com/pizza.jpg"), recipe.image)
        assertEquals("Delicious pizza", recipe.summary)
            }

    @Test
    fun `mapFromFavoriteEntity maps correctly with null image`() {
        val entity = FavoriteRecipeEntity(
            id = 2L,
            title = "Soup",
            image = null,
            summary = "Warm soup"
        )

        val recipe = RecipeMapper.mapFromFavoriteEntity(entity)

        assertNull(recipe.image)
            }

    @Test
    fun `mapFromFavoriteEntity maps correctly with invalid image URL`() {
        val entity = FavoriteRecipeEntity(
            id = 3L,
            title = "Test",
            image = "not-a-url",
            summary = "Test"
        )

        val recipe = RecipeMapper.mapFromFavoriteEntity(entity)

        assertNull(recipe.image)
    }

    @Test
    fun `mapToFavoriteEntity maps correctly`() {
        val recipe = Recipe(
            id = 1L,
            title = "Pizza",
            image = URL("https://example.com/pizza.jpg"),
            summary = "Delicious",
            )

        val entity = RecipeMapper.mapToFavoriteEntity(recipe)

        assertEquals(1L, entity.id)
        assertEquals("Pizza", entity.title)
        assertEquals("https://example.com/pizza.jpg", entity.image)
        assertEquals("Delicious", entity.summary)
    }

    @Test
    fun `mapToFavoriteEntity maps null image`() {
        val recipe = Recipe(
            id = 1L,
            title = "Soup",
            image = null,
            summary = "Warm",
            )

        val entity = RecipeMapper.mapToFavoriteEntity(recipe)

        assertNull(entity.image)
    }

    @Test
    fun `mapFromRecipeResponse maps correctly`() {
        val response = RecipeResponse(
            id = 1L,
            title = "Pasta",
            image = "https://example.com/pasta.jpg",
            summary = "Tasty pasta"
        )

        val recipe = RecipeMapper.mapFromRecipeResponse(response)

        assertEquals(1L, recipe.id)
        assertEquals("Pasta", recipe.title)
        assertEquals(URL("https://example.com/pasta.jpg"), recipe.image)
        assertEquals("Tasty pasta", recipe.summary)
            }

    @Test
    fun `mapFromRecipePageResponse maps all results`() {
        val response = RecipePageResponse(
            results = listOf(
                RecipeResponse(id = 1L, title = "R1", image = "https://example.com/1.jpg", summary = "S1"),
                RecipeResponse(id = 2L, title = "R2", image = "https://example.com/2.jpg", summary = "S2")
            ),
            totalResults = 100
        )

        val recipePage = RecipeMapper.mapFromRecipePageResponse(response)

        assertEquals(100, recipePage.totalResults)
        assertEquals(2, recipePage.list.size)
        assertEquals("R1", recipePage.list[0]?.title)
        assertEquals("R2", recipePage.list[1]?.title)
    }

    @Test
    fun `mapFromStoredRecipe maps correctly`() {
        val entity = RecipeEntity(
            index = 0,
            id = 1L,
            title = "Stored Recipe",
            image = "https://example.com/stored.jpg",
            summary = "Stored summary"
        )

        val recipe = RecipeMapper.mapFromStoredRecipe(entity)

        assertEquals(1L, recipe.id)
        assertEquals("Stored Recipe", recipe.title)
        assertEquals(URL("https://example.com/stored.jpg"), recipe.image)
        assertEquals("Stored summary", recipe.summary)
            }

    @Test
    fun `mapFromStoredRecipes maps list correctly`() {
        val entities = listOf(
            RecipeEntity(index = 0, id = 1L, title = "R1", image = "https://example.com/1.jpg", summary = "S1"),
            RecipeEntity(index = 1, id = 2L, title = "R2", image = null, summary = "S2")
        )

        val recipes = RecipeMapper.mapFromStoredRecipes(entities)

        assertEquals(2, recipes.size)
        assertEquals("R1", recipes[0].title)
        assertNull(recipes[1].image)
    }

    @Test
    fun `mapToStoredRecipes filters null recipes`() {
        val recipes = listOf(
            Recipe(id = 1L, title = "R1", image = URL("https://example.com/1.jpg"), summary = "S1", ),
            null,
            Recipe(id = 3L, title = "R3", image = null, summary = "S3", )
        )

        val entities = RecipeMapper.mapToStoredRecipes(recipes)

        assertEquals(2, entities.size)
        assertEquals(1L, entities[0].id)
        assertEquals(3L, entities[1].id)
    }

    @Test
    fun `mapToStoredRecipes sets empty string for null image`() {
        val recipes = listOf(
            Recipe(id = 1L, title = "R1", image = null, summary = "S1", )
        )

        val entities = RecipeMapper.mapToStoredRecipes(recipes)

        assertEquals("", entities[0].image)
    }

    @Test
    fun `mapSortCriteria RELEVANCE returns null`() {
        assertNull(RecipeMapper.mapSortCriteria(RecipeSortCriteria.RELEVANCE))
    }

    @Test
    fun `mapSortCriteria POPULARITY returns popularity`() {
        assertEquals("popularity", RecipeMapper.mapSortCriteria(RecipeSortCriteria.POPULARITY))
    }

    @Test
    fun `mapSortCriteria PREPARATION_TIME returns time`() {
        assertEquals("time", RecipeMapper.mapSortCriteria(RecipeSortCriteria.PREPARATION_TIME))
    }

    @Test
    fun `mapSortCriteria CALORIES returns calories`() {
        assertEquals("calories", RecipeMapper.mapSortCriteria(RecipeSortCriteria.CALORIES))
    }

    @Test
    fun `mapSortOrder ASCENDING returns asc`() {
        assertEquals("asc", RecipeMapper.mapSortOrder(RecipeSortOrder.ASCENDING))
    }

    @Test
    fun `mapSortOrder DESCENDING returns desc`() {
        assertEquals("desc", RecipeMapper.mapSortOrder(RecipeSortOrder.DESCENDING))
    }
}
