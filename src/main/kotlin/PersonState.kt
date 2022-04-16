/**
 * Describes the state a [Person] can be in.
 */
sealed class PersonState {
    object Started: PersonState()
    class Departed(val destination: Station): PersonState()
    class Arrived(val destination: Station): PersonState()
    object Finished: PersonState()
}