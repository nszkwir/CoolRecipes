package com.spitzer.data.repository.recipe.mapper

import com.spitzer.data.local.recipe.entities.RecipeDetailsEntity
import com.spitzer.data.remote.recipe.api.dto.RecipeDetailsResponse
import com.spitzer.domain.model.recipe.RecipeDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.net.URL

class RecipeDetailsMapperTest {

    @Test
    fun `mapFromRecipeDetailsResponse maps all fields correctly`() {
        val response = RecipeDetailsResponse(
            id = 1L,
            title = "Test Recipe",
            readyInMinutes = 30,
            servings = 4,
            summary = "A test recipe",
            instructions = "Cook it",
            vegetarian = false,
            vegan = false,
            glutenFree = true,
            dairyFree = false,
            image = "https://example.com/recipe.jpg",
            healthScore = 75.0,
            diets = listOf("gluten free"),
            spoonacularScore = 85.0,
            spoonacularSourceUrl = "https://spoonacular.com/recipe-1",
            extendedIngredients = listOf(
                RecipeDetailsResponse.Ingredient(original = "1 cup flour", image = null),
                RecipeDetailsResponse.Ingredient(original = "2 eggs", image = "egg.png")
            )
        )

        val result = RecipeDetailsMapper.mapFromRecipeDetailsResponse(response)

        assertEquals(1L, result.id)
        assertEquals("Test Recipe", result.title)
                assertEquals(30, result.readyInMinutes)
        assertEquals(4, result.servings)
        assertEquals("A test recipe", result.summary)
        assertEquals("Cook it", result.instructions)
        assertEquals(false, result.vegetarian)
        assertEquals(false, result.vegan)
        assertEquals(true, result.glutenFree)
        assertEquals(false, result.dairyFree)
        assertEquals(URL("https://example.com/recipe.jpg"), result.image)
        assertEquals(75.0, result.healthScore)
        assertEquals(listOf("gluten free"), result.diets)
        assertEquals(85.0, result.spoonacularScore, 0.001)
        assertEquals(URL("https://spoonacular.com/recipe-1"), result.spoonacularSourceUrl)
        assertEquals(listOf("1 cup flour", "2 eggs"), result.ingredients)
    }

    @Test
    fun `mapFromRecipeDetailsResponse handles null diets`() {
        val response = createMinimalResponse(diets = null)

        val result = RecipeDetailsMapper.mapFromRecipeDetailsResponse(response)

        assertEquals(emptyList<String>(), result.diets)
    }

    @Test
    fun `mapFromRecipeDetailsResponse handles null extendedIngredients`() {
        val response = createMinimalResponse(extendedIngredients = null)

        val result = RecipeDetailsMapper.mapFromRecipeDetailsResponse(response)

        assertEquals(emptyList<String>(), result.ingredients)
    }

    @Test
    fun `mapFromRecipeDetailsResponse filters null ingredient originals`() {
        val response = createMinimalResponse(
            extendedIngredients = listOf(
                RecipeDetailsResponse.Ingredient(original = "flour", image = null),
                RecipeDetailsResponse.Ingredient(original = null, image = "img.png"),
                RecipeDetailsResponse.Ingredient(original = "sugar", image = null)
            )
        )

        val result = RecipeDetailsMapper.mapFromRecipeDetailsResponse(response)

        assertEquals(listOf("flour", "sugar"), result.ingredients)
    }

    @Test
    fun `mapFromRecipeDetailsResponse handles null image`() {
        val response = createMinimalResponse(image = null)

        val result = RecipeDetailsMapper.mapFromRecipeDetailsResponse(response)

        assertNull(result.image)
    }

    @Test
    fun `mapFromRecipeDetailsResponse handles invalid image URL`() {
        val response = createMinimalResponse(image = "not-a-url")

        val result = RecipeDetailsMapper.mapFromRecipeDetailsResponse(response)

        assertNull(result.image)
    }

    @Test
    fun `mapFromStoredRecipeDetails maps all fields correctly`() {
        val entity = RecipeDetailsEntity(
            id = 1L,
            title = "Stored Recipe",
            readyInMinutes = 20,
            servings = 2,
            summary = "Stored summary",
            instructions = "Stored instructions",
            vegetarian = true,
            vegan = true,
            glutenFree = true,
            dairyFree = true,
            image = "https://example.com/stored.jpg",
            healthScore = 90.0,
            diets = listOf("vegan"),
            spoonacularScore = 95.0,
            spoonacularSourceUrl = "https://spoonacular.com/stored",
            ingredients = listOf("lettuce", "tomato")
        )

        val result = RecipeDetailsMapper.mapFromStoredRecipeDetails(entity)

        assertEquals(1L, result.id)
        assertEquals("Stored Recipe", result.title)
                assertEquals(20, result.readyInMinutes)
        assertEquals(URL("https://example.com/stored.jpg"), result.image)
        assertEquals(listOf("lettuce", "tomato"), result.ingredients)
    }

    @Test
    fun `mapToStoredRecipeDetails maps all fields correctly`() {
        val recipeDetails = RecipeDetails(
            id = 1L,
            title = "Domain Recipe",
            readyInMinutes = 25,
            servings = 3,
            summary = "Domain summary",
            instructions = "Domain instructions",
            vegetarian = false,
            vegan = false,
            glutenFree = false,
            dairyFree = false,
            image = URL("https://example.com/domain.jpg"),
            healthScore = 50.0,
            diets = listOf("paleo"),
            spoonacularScore = 60.0,
            spoonacularSourceUrl = URL("https://spoonacular.com/domain"),
            ingredients = listOf("chicken", "rice")
        )

        val result = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)

        assertEquals(1L, result.id)
        assertEquals("Domain Recipe", result.title)
        assertEquals(25, result.readyInMinutes)
        assertEquals("https://example.com/domain.jpg", result.image)
        assertEquals(listOf("chicken", "rice"), result.ingredients)
        assertEquals("https://spoonacular.com/domain", result.spoonacularSourceUrl)
    }

    @Test
    fun `mapToStoredRecipeDetails handles null image`() {
        val recipeDetails = createMinimalRecipeDetails(image = null)

        val result = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)

        assertEquals("", result.image)
    }

    @Test
    fun `mapToStoredRecipeDetails handles null spoonacularSourceUrl`() {
        val recipeDetails = createMinimalRecipeDetails(spoonacularSourceUrl = null)

        val result = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)

        assertEquals("", result.spoonacularSourceUrl)
    }

    @Test
    fun `mapToStoredRecipeDetails handles null diets`() {
        val recipeDetails = createMinimalRecipeDetails(diets = null)

        val result = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)

        assertEquals(emptyList<String>(), result.diets)
    }

    @Test
    fun `roundtrip response to entity and back preserves data`() {
        val response = RecipeDetailsResponse(
            id = 42L,
            title = "Roundtrip Recipe",
            readyInMinutes = 45,
            servings = 6,
            summary = "Roundtrip summary",
            instructions = "Roundtrip instructions",
            vegetarian = true,
            vegan = false,
            glutenFree = true,
            dairyFree = false,
            image = "https://example.com/roundtrip.jpg",
            healthScore = 80.0,
            diets = listOf("vegetarian"),
            spoonacularScore = 88.0,
            spoonacularSourceUrl = "https://spoonacular.com/roundtrip",
            extendedIngredients = listOf(
                RecipeDetailsResponse.Ingredient(original = "cheese", image = null)
            )
        )

        val domain = RecipeDetailsMapper.mapFromRecipeDetailsResponse(response)
        val entity = RecipeDetailsMapper.mapToStoredRecipeDetails(domain)
        val restored = RecipeDetailsMapper.mapFromStoredRecipeDetails(entity)

        assertEquals(domain.id, restored.id)
        assertEquals(domain.title, restored.title)
        assertEquals(domain.readyInMinutes, restored.readyInMinutes)
        assertEquals(domain.servings, restored.servings)
        assertEquals(domain.summary, restored.summary)
        assertEquals(domain.instructions, restored.instructions)
        assertEquals(domain.vegetarian, restored.vegetarian)
        assertEquals(domain.vegan, restored.vegan)
        assertEquals(domain.glutenFree, restored.glutenFree)
        assertEquals(domain.dairyFree, restored.dairyFree)
        assertEquals(domain.image, restored.image)
        assertEquals(domain.healthScore, restored.healthScore)
        assertEquals(domain.diets, restored.diets)
        assertEquals(domain.spoonacularScore, restored.spoonacularScore, 0.001)
        assertEquals(domain.spoonacularSourceUrl, restored.spoonacularSourceUrl)
        assertEquals(domain.ingredients, restored.ingredients)
    }

    // Helper functions

    private fun createMinimalResponse(
        image: String? = "https://example.com/img.jpg",
        diets: List<String>? = emptyList(),
        extendedIngredients: List<RecipeDetailsResponse.Ingredient>? = emptyList()
    ) = RecipeDetailsResponse(
        id = 1L,
        title = "Test",
        readyInMinutes = 10,
        servings = 1,
        summary = "Summary",
        instructions = "Instructions",
        vegetarian = false,
        vegan = false,
        glutenFree = false,
        dairyFree = false,
        image = image,
        healthScore = null,
        diets = diets,
        spoonacularScore = 0.0,
        spoonacularSourceUrl = null,
        extendedIngredients = extendedIngredients
    )

    private fun createMinimalRecipeDetails(
        image: URL? = URL("https://example.com/img.jpg"),
        spoonacularSourceUrl: URL? = URL("https://spoonacular.com/1"),
        diets: List<String>? = emptyList()
    ) = RecipeDetails(
        id = 1L,
        title = "Test",
        readyInMinutes = 10,
        servings = 1,
        summary = "Summary",
        instructions = "Instructions",
        vegetarian = false,
        vegan = false,
        glutenFree = false,
        dairyFree = false,
        image = image,
        healthScore = null,
        diets = diets,
        spoonacularScore = 0.0,
        spoonacularSourceUrl = spoonacularSourceUrl,
        ingredients = emptyList()
    )
}
