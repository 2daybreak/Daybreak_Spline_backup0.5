
open class Nurbs(private val maxDeg: Int): Bspline(maxDeg) {
    /*
    A Nurbs curve is defined by
    C(u) = Sigma Ni(u) * wi * Pi / Sigma Ni(u) * wi
    */

    val wt = mutableListOf<Double>()

}