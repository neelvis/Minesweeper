data class City(val name: String, private val averageTemperature: Int) {
    var degrees: Int = 0
        set(value) {
            field = if (value !in -92..57) averageTemperature else value
        }
}        

fun main() {
    val first = readLine()!!.toInt()
    val second = readLine()!!.toInt()
    val third = readLine()!!.toInt()
    val firstCity = City("Dubai", 30)
    firstCity.degrees = first
    val secondCity = City("Moscow", 5)
    secondCity.degrees = second
    val thirdCity = City("Hanoi", 20)
    thirdCity.degrees = third

    val cities = listOf<City>(firstCity, secondCity, thirdCity)
        .sortedBy(City::degrees)
        .distinctBy { it.degrees }
    print(if (cities.size < 3) "neither" else cities.first().name)
}
