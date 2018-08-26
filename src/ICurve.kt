interface ICurve {

    fun addPts(v: Vector3)
    fun addPts(i: Int, v: Vector3)
    fun removePts(v: Vector3)
    fun removePts(i: Int)
}