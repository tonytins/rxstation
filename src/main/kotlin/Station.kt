import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class Station(
    val name: String,
    val connection: Map<Station, Route> = mutableMapOf()
) {
    val people: MutableList<Person> = mutableListOf()

    private val mutConnections get() = connection as? MutableMap<Station, Route>

    fun connect(
        other: Station,
        distance: Int = Random.nextInt(2, 10)
    ) {
        // mutConnections!![other] =
    }

    private fun connectOneWay(other: Station, distance: Int) = Route(distance).apply {
        exit.observeOn(Schedulers.io())
            .subscribe {
                it.onArrive(other)
            }
    }

    fun findNextStation(target: Station): Station? {
        return null
    }

    private fun findStation(
        targetStation: Station,
        visitedStations: MutableList<Station> = mutableListOf()
    ): Station? {
        visitedStations += this
        val unvisitedStations = connection.keys.filterNot {
            visitedStations.contains(it)
        }

        Completable.merge(people.map {  it.personLife })
            .blockingAwait()
    }
}