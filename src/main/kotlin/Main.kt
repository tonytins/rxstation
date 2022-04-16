fun main(args: Array<String>) {
    // Generate the initial list of people
    val simCount = args.getOrNull(0)?.toIntOrNull() ?: 20
    val sims = List(simCount) { Person("Sim $it") }


}