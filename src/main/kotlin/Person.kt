import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Represents a person that can freely move between different [City] entities.
 */
data class Person(
    val name: String,
) {
    private val stateSubject: BehaviorSubject<PersonState> = BehaviorSubject.create()

    /**
     * An [Observable] that emits no items, only completes when the person has arrived at their
     * target destination.
     */
    val personLife = stateSubject.ignoreElements()

    /**
     * An [Observable] that emits String descriptions of what is happening to this person, which
     * can be echoed to the terminal or processed in some other way.
     */
    val readableStatus: PublishSubject<String> = PublishSubject.create()

    var previousCity: Station? = null
    var city: Station? = null
        set(value) {
            field?.people?.remove(this)
            field = value
            field?.people?.add(this)
        }

    lateinit var targetCity: Station
        private set

    init {
        stateSubject.observeOn(Schedulers.io())
            .subscribe { state ->
                when (state) {
                    PersonState.Started -> departForNextCity()
                    is PersonState.Departed -> {
                        readableStatus.onNext("left ${city?.name}")
                        traverse(state.destination)
                    }
                    is PersonState.Arrived -> {
                        readableStatus.onNext("arrived at ${state.destination.name}")
                        this.city = state.destination
                        departForNextCity()
                    }
                    PersonState.Finished -> {
                        readableStatus.onNext("is dead")
                        stateSubject.onComplete()
                    }
                }
            }
    }

    fun start(targetCity: Station) {
        this.targetCity = targetCity
        stateSubject.onNext(PersonState.Started)
    }

    private fun departForNextCity() {
        if (city == targetCity) {
            stateSubject.onNext(PersonState.Finished)
        } else {
            stateSubject.onNext(PersonState.Departed(city!!.findNextStation(targetCity)!!))
        }
    }

    fun traverse(newStation: Station) {
        previousCity = city
        when {
            city == null -> error("Already traversing")
            !city!!.connections.containsKey(newStation) -> error("Traversing to an inaccessible city: $previousCity -> $newStation")
        }
        city!!.connections[newStation]!!.enter.onNext(this)
        this.city = null
    }

    fun onArrive(destination: Station) {
        stateSubject.onNext(PersonState.Arrived(destination))
    }
}